package com.memoryshade.domain.chat.service;

import com.memoryshade.domain.chat.dto.ChatMediaReadResponseDto;
import com.memoryshade.domain.chat.dto.ChatMediaUploadResponseDto;
import com.memoryshade.domain.chat.dto.ChatMessageResponseDto;
import com.memoryshade.domain.chat.dto.ChatMessagesReadResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCloseResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCreateResponseDto;
import com.memoryshade.domain.chat.dto.ChatTextRequestDto;
import com.memoryshade.domain.chat.dto.ChatVoiceResponseDto;
import com.memoryshade.domain.chat.exception.ChatErrorCode;
import com.memoryshade.domain.chat.model.ChatMessage;
import com.memoryshade.domain.chat.model.ChatMessageType;
import com.memoryshade.domain.chat.model.ChatSession;
import com.memoryshade.domain.chat.model.ChatSessionMedia;
import com.memoryshade.domain.chat.model.ConversationStage;
import com.memoryshade.domain.chat.model.SenderType;
import com.memoryshade.domain.chat.repository.ChatMessageRepository;
import com.memoryshade.domain.chat.repository.ChatSessionMediaRepository;
import com.memoryshade.domain.chat.repository.ChatSessionRepository;
import com.memoryshade.domain.diary.dto.DiaryCreateFromChatResponseDto;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.MediaType;
import com.memoryshade.domain.diary.service.DiaryMediaService;
import com.memoryshade.domain.diary.service.DiaryService;
import com.memoryshade.domain.emotion.service.EmotionService;
import com.memoryshade.domain.recall.model.RecallQuizAnswer;
import com.memoryshade.domain.recall.model.RecallQuizQuestion;
import com.memoryshade.domain.recall.service.RecallQuizService;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import com.memoryshade.global.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

  private static final String DEFAULT_INITIAL_QUESTION = "안녕하세요. 오늘은 어떤 하루를 보내셨나요?";
  private static final String RECALL_QUIZ_TRANSITION_MESSAGE =
      "회상 질문이 끝났어요. 이제 오늘 하루는 어떠셨는지 편하게 말씀해 주세요.";

  private static final String INITIAL_QUESTION_SYSTEM_PROMPT = """
      당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
      짧고 부드럽게 말하세요. 한 번에 질문은 하나만 하세요.
      항상 공감부터 하고, 자연스럽게 회상을 유도하세요.
      전날 기록을 참고해서 오늘 대화를 시작하는 첫 질문을 한 문장으로 생성하세요.
      """;

  private static final String CHAT_SYSTEM_PROMPT = """
      당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
      짧고 부드럽게 말하고, 한 번에 질문은 하나만 하세요.
      항상 공감 후 질문하세요.
      """;

  private static final String SUMMARY_SYSTEM_PROMPT = """
      당신은 경증 치매 어르신의 하루 대화를 요약하는 AI입니다.
      사용자의 하루를 중심으로 핵심 사건, 감정, 활동을 2~3문장으로 간단히 요약하세요.
      불필요한 설명 없이 일기 저장용 요약만 작성하세요.
      """;

  private final ChatSessionRepository sessionRepository;
  private final ChatSessionMediaRepository chatSessionMediaRepository;
  private final ChatMessageRepository messageRepository;
  private final UserRepository userRepository;
  private final DiaryService diaryService;
  private final DiaryMediaService diaryMediaService;
  private final EmotionService emotionService;
  private final RecallQuizService recallQuizService;
  private final OpenAiAudioTranscriptionModel sttModel;
  private final ChatClient chatClient;
  private final FileStorageService fileStorageService;

  @Transactional
  public ChatSessionCreateResponseDto createChatSession(Long loginUserId) {
    validateUserId(loginUserId);
    User user = userRepository.getByUserId(loginUserId);
    LocalDate today = LocalDate.now();

    ChatSession session = sessionRepository
        .findByUser_UserIdAndSessionDateAndIsActiveTrue(loginUserId, today)
        .orElseGet(() -> sessionRepository.save(
            ChatSession.builder()
                .user(user)
                .sessionDate(today)
                .conversationStage(ConversationStage.DAILY_RECORD)
                .build()
        ));

    List<ChatMessage> existingMessages =
        messageRepository.findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId());

    if (!existingMessages.isEmpty()) {
      return new ChatSessionCreateResponseDto(
          session.getSessionId(),
          session.getSessionDate(),
          existingMessages.stream()
              .map(ChatMessageResponseDto::from)
              .toList()
      );
    }

    if (shouldStartRecallQuiz(session)) {
      session.changeToRecallQuizStage();
      recallQuizService.createRecallQuiz(loginUserId, session.getSessionId());

      RecallQuizQuestion firstQuestion = recallQuizService.getCurrentQuestion(session.getSessionId());

      if (firstQuestion != null) {
        ChatMessage initialAiMessage = saveAiMessage(
            session,
            firstQuestion.getQuestionText(),
            ChatMessageType.RECALL_QUIZ,
            firstQuestion.getReferenceMediaUrl()
        );

        return new ChatSessionCreateResponseDto(
            session.getSessionId(),
            session.getSessionDate(),
            List.of(ChatMessageResponseDto.from(initialAiMessage))
        );
      }

      session.changeToDailyRecordStage();
    }

    String initialQuestion = getInitialQuestion(loginUserId);
    ChatMessage initialAiMessage = saveAiMessage(
        session,
        initialQuestion,
        ChatMessageType.NORMAL,
        null
    );

    return new ChatSessionCreateResponseDto(
        session.getSessionId(),
        session.getSessionDate(),
        List.of(ChatMessageResponseDto.from(initialAiMessage))
    );
  }

  @Transactional
  public ChatVoiceResponseDto createVoiceChatMessage(Long loginUserId, Long sessionId, MultipartFile file) {
    validateUserId(loginUserId);
    if (file == null || file.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_AUDIO_FILE);
    }

    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);
    String userText = transcribe(file);

    return processChatInteraction(session, userText);
  }

  @Transactional
  public ChatVoiceResponseDto createTextChatMessage(Long loginUserId, Long sessionId, ChatTextRequestDto request) {
    validateUserId(loginUserId);
    if (request == null || request.content() == null || request.content().isBlank()) {
      throw new ExceptionList(ChatErrorCode.TEXT_MESSAGE_EMPTY);
    }

    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);
    return processChatInteraction(session, request.content().trim());
  }

  @Transactional
  public ChatMediaUploadResponseDto uploadChatMedia(Long loginUserId, Long sessionId, List<MultipartFile> files) {
    validateUserId(loginUserId);
    if (files == null || files.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_IMAGE_FILE);
    }

    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);

    List<String> mediaUrls = files.stream()
        .filter(file -> file != null && !file.isEmpty())
        .map(file -> {
          String url = fileStorageService.uploadImage(file);

          chatSessionMediaRepository.save(
              ChatSessionMedia.builder()
                  .session(session)
                  .mediaUrl(url)
                  .mediaType(MediaType.IMAGE)
                  .build()
          );

          return url;
        })
        .toList();

    if (mediaUrls.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_IMAGE_FILE);
    }

    return new ChatMediaUploadResponseDto(mediaUrls);
  }

  @Transactional
  public ChatSessionCloseResponseDto closeChatSession(Long loginUserId, Long sessionId) {
    validateUserId(loginUserId);
    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);

    List<ChatMessage> messages =
        messageRepository.findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId());

    if (messages.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND);
    }

    String contentStt = buildContentStt(messages);
    String contentSummary = generateChatSummary(messages);

    DiaryCreateFromChatResponseDto diaryResponse = diaryService.createDiaryFromChat(
        loginUserId,
        contentStt,
        contentSummary,
        session.getSessionDate()
    );

    List<ChatSessionMedia> chatSessionMedias =
        chatSessionMediaRepository.findAllBySession_SessionIdOrderByCreatedAtAsc(sessionId);

    if (!chatSessionMedias.isEmpty()) {
      diaryMediaService.createDiaryMediasFromChatSession(diaryResponse.diaryId(), chatSessionMedias);
    }

    emotionService.createEmotionAnalysis(loginUserId, diaryResponse.diaryId());
    session.close();

    return new ChatSessionCloseResponseDto(
        diaryResponse.diaryId(),
        diaryResponse.diaryDate(),
        diaryResponse.contentSummary()
    );
  }

  @Transactional(readOnly = true)
  public ChatMessagesReadResponseDto getChatMessages(Long loginUserId, Long sessionId) {
    validateUserId(loginUserId);
    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);

    List<ChatMessageResponseDto> messages = messageRepository
        .findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
        .stream()
        .map(ChatMessageResponseDto::from)
        .toList();

    List<ChatMediaReadResponseDto> medias = chatSessionMediaRepository
        .findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
        .stream()
        .map(ChatMediaReadResponseDto::from)
        .toList();

    return new ChatMessagesReadResponseDto(messages, medias);
  }

  private void validateUserId(Long userId) {
    if (userId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }
  }

  private ChatVoiceResponseDto processChatInteraction(ChatSession session, String userText) {
    ChatMessage userMessage = saveUserMessage(session, userText);

    if (session.getConversationStage() == ConversationStage.RECALL_QUIZ) {
      return processRecallQuizInteraction(session, userMessage);
    }

    String aiText = generateAiResponse(session);
    ChatMessage aiMessage = saveAiMessage(
        session,
        aiText,
        ChatMessageType.NORMAL,
        null
    );

    return new ChatVoiceResponseDto(
        session.getSessionId(),
        List.of(
            ChatMessageResponseDto.from(userMessage),
            ChatMessageResponseDto.from(aiMessage)
        )
    );
  }


  private ChatVoiceResponseDto processRecallQuizInteraction(ChatSession session, ChatMessage userMessage) {
    RecallQuizQuestion currentQuestion = recallQuizService.getCurrentQuestion(session.getSessionId());

    RecallQuizAnswer answer = recallQuizService.evaluateAnswer(
        currentQuestion,
        userMessage.getContent()
    );

    recallQuizService.applyResult(
        session.getSessionId(),
        answer.getJudgement()
    );

    if (recallQuizService.isCompleted(session.getSessionId())) {
      session.changeToDailyRecordStage();

      ChatMessage transitionMessage = saveAiMessage(
          session,
          "회상 질문이 끝났어요. 이제 오늘 하루는 어떠셨는지 편하게 말씀해 주세요.",
          ChatMessageType.STAGE_TRANSITION,
          null
      );

      return new ChatVoiceResponseDto(
          session.getSessionId(),
          List.of(
              ChatMessageResponseDto.from(userMessage),
              ChatMessageResponseDto.from(transitionMessage)
          )
      );
    }

    RecallQuizQuestion nextQuestion = recallQuizService.getCurrentQuestion(session.getSessionId());

    ChatMessage nextAiMessage = saveAiMessage(
        session,
        nextQuestion.getQuestionText(),
        ChatMessageType.RECALL_QUIZ,
        nextQuestion.getReferenceMediaUrl()
    );

    return new ChatVoiceResponseDto(
        session.getSessionId(),
        List.of(
            ChatMessageResponseDto.from(userMessage),
            ChatMessageResponseDto.from(nextAiMessage)
        )
    );
  }

  private ChatSession getOwnedActiveSession(Long loginUserId, Long sessionId) {
    userRepository.getByUserId(loginUserId);

    ChatSession session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new ExceptionList(ChatErrorCode.CHAT_SESSION_NOT_FOUND));

    if (!session.getUser().getUserId().equals(loginUserId)) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    if (!session.isActive()) {
      throw new ExceptionList(ChatErrorCode.CHAT_SESSION_CLOSED);
    }

    return session;
  }

  private boolean shouldStartRecallQuiz(ChatSession session) {
    return !recallQuizService.hasRecallQuiz(session.getSessionId());
  }

  private String getInitialQuestion(Long userId) {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    return diaryService.findTopDiaryByUserIdAndDiaryDate(userId, yesterday)
        .map(Diary::getContentSummary)
        .filter(summary -> !summary.isBlank())
        .map(this::generateInitialQuestionFromAi)
        .orElse(DEFAULT_INITIAL_QUESTION);
  }

  private String generateInitialQuestionFromAi(String previousSummary) {
    try {
      String content = chatClient.prompt()
          .messages(List.of(
              new SystemMessage(INITIAL_QUESTION_SYSTEM_PROMPT),
              new UserMessage("전날 일기 요약:\n" + previousSummary)
          ))
          .call()
          .content();

      return (content == null || content.isBlank()) ? DEFAULT_INITIAL_QUESTION : content;
    } catch (Exception e) {
      return DEFAULT_INITIAL_QUESTION;
    }
  }

  private String transcribe(MultipartFile file) {
    try {
      Resource resource = new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() {
          return file.getOriginalFilename();
        }
      };

      String result = sttModel.call(resource);

      if (result == null || result.isBlank()) {
        throw new ExceptionList(ChatErrorCode.STT_FAILED);
      }

      return result;
    } catch (Exception e) {
      throw new ExceptionList(ChatErrorCode.STT_FAILED);
    }
  }

  private String generateAiResponse(ChatSession session) {
    try {
      List<Message> messages = new ArrayList<>(
          messageRepository.findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
              .stream()
              .map(this::toAiMessage)
              .toList()
      );

      messages.add(0, new SystemMessage(CHAT_SYSTEM_PROMPT));

      String result = chatClient.prompt()
          .messages(messages)
          .call()
          .content();

      if (result == null || result.isBlank()) {
        throw new ExceptionList(ChatErrorCode.AI_RESPONSE_FAILED);
      }

      return result;
    } catch (Exception e) {
      throw new ExceptionList(ChatErrorCode.AI_RESPONSE_FAILED);
    }
  }

  private Message toAiMessage(ChatMessage message) {
    if (message.getSenderType() == SenderType.USER) {
      return new UserMessage(message.getContent());
    }
    return new AssistantMessage(message.getContent());
  }

  private String buildContentStt(List<ChatMessage> messages) {
    return messages.stream()
        .filter(message -> message.getSenderType() == SenderType.USER)
        .map(ChatMessage::getContent)
        .collect(Collectors.joining("\n"));
  }

  private String generateChatSummary(List<ChatMessage> messages) {
    try {
      String conversation = messages.stream()
          .map(message -> message.getSenderType() + ": " + message.getContent())
          .collect(Collectors.joining("\n"));

      String result = chatClient.prompt()
          .messages(List.of(
              new SystemMessage(SUMMARY_SYSTEM_PROMPT),
              new UserMessage("대화 내용:\n" + conversation)
          ))
          .call()
          .content();

      if (result == null || result.isBlank()) {
        throw new ExceptionList(ChatErrorCode.AI_RESPONSE_FAILED);
      }

      return result;
    } catch (Exception e) {
      throw new ExceptionList(ChatErrorCode.AI_RESPONSE_FAILED);
    }
  }

  private ChatMessage saveUserMessage(ChatSession session, String content) {
    return messageRepository.save(
        ChatMessage.builder()
            .session(session)
            .senderType(SenderType.USER)
            .content(content)
            .messageType(ChatMessageType.NORMAL)
            .referenceMediaUrl(null)
            .build()
    );
  }

  private ChatMessage saveAiMessage(
      ChatSession session,
      String content,
      ChatMessageType messageType,
      String referenceMediaUrl
  ) {
    return messageRepository.save(
        ChatMessage.builder()
            .session(session)
            .senderType(SenderType.AI)
            .content(content)
            .messageType(messageType)
            .referenceMediaUrl(referenceMediaUrl)
            .build()
    );
  }
}