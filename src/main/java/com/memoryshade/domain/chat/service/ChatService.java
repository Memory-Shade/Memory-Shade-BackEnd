package com.memoryshade.domain.chat.service;

import com.memoryshade.domain.chat.dto.ChatMediaUploadResponseDto;
import com.memoryshade.domain.chat.dto.ChatMessageResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCloseResponseDto;
import com.memoryshade.domain.chat.dto.ChatSessionCreateResponseDto;
import com.memoryshade.domain.chat.dto.ChatVoiceResponseDto;
import com.memoryshade.domain.chat.exception.ChatErrorCode;
import com.memoryshade.domain.chat.model.ChatMessage;
import com.memoryshade.domain.chat.model.ChatSession;
import com.memoryshade.domain.chat.model.ChatSessionMedia;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

  private static final String DEFAULT_INITIAL_QUESTION = "안녕하세요. 오늘은 어떤 하루를 보내셨나요?";

  private final ChatSessionRepository sessionRepository;
  private final ChatSessionMediaRepository chatSessionMediaRepository;
  private final ChatMessageRepository messageRepository;
  private final UserRepository userRepository;
  private final DiaryService diaryService;
  private final DiaryMediaService diaryMediaService;
  private final EmotionService emotionService;
  private final OpenAiAudioTranscriptionModel sttModel;
  private final ChatClient chatClient;
  private final FileStorageService fileStorageService;

  @Transactional
  public ChatSessionCreateResponseDto createChatSession(Long loginUserId) {
    if (loginUserId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    User user = userRepository.getByUserId(loginUserId);
    LocalDate today = LocalDate.now();

    ChatSession session = sessionRepository.findByUser_UserIdAndSessionDate(loginUserId, today)
        .orElseGet(() -> sessionRepository.save(
            ChatSession.builder()
                .user(user)
                .sessionDate(today)
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

    String previousDiarySummary = getYesterdayDiarySummary(loginUserId);

    String initialQuestion = previousDiarySummary == null || previousDiarySummary.isBlank()
        ? DEFAULT_INITIAL_QUESTION
        : generateInitialQuestion(previousDiarySummary);

    ChatMessage initialAiMessage = saveMessage(session, SenderType.AI, initialQuestion);

    return new ChatSessionCreateResponseDto(
        session.getSessionId(),
        session.getSessionDate(),
        List.of(ChatMessageResponseDto.from(initialAiMessage))
    );
  }

  @Transactional
  public ChatVoiceResponseDto createVoiceChatMessage(
      Long loginUserId,
      Long sessionId,
      MultipartFile file
  ) {
    if (loginUserId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    if (file == null || file.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_AUDIO_FILE);
    }

    ChatSession session = getOwnedSession(loginUserId, sessionId);

    String userText = transcribe(file);

    ChatMessage userMessage = saveMessage(session, SenderType.USER, userText);

    String aiText = generateAiResponse(session);

    ChatMessage aiMessage = saveMessage(session, SenderType.AI, aiText);

    return new ChatVoiceResponseDto(
        session.getSessionId(),
        List.of(
            ChatMessageResponseDto.from(userMessage),
            ChatMessageResponseDto.from(aiMessage)
        )
    );
  }

  @Transactional
  public ChatMediaUploadResponseDto uploadChatMedia(
      Long loginUserId,
      Long sessionId,
      MultipartFile file
  ) {
    if (loginUserId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    if (file == null || file.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_IMAGE_FILE);
    }

    ChatSession session = getOwnedSession(loginUserId, sessionId);

    String mediaUrl = fileStorageService.uploadImage(file);

    chatSessionMediaRepository.save(
        ChatSessionMedia.builder()
            .session(session)
            .mediaUrl(mediaUrl)
            .mediaType(MediaType.IMAGE)
            .build()
    );

    return new ChatMediaUploadResponseDto(mediaUrl);
  }

  @Transactional
  public ChatSessionCloseResponseDto closeChatSession(
      Long loginUserId,
      Long sessionId
  ) {
    if (loginUserId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    ChatSession session = getOwnedSession(loginUserId, sessionId);

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
      diaryMediaService.createDiaryMediasFromChatSession(
          diaryResponse.diaryId(),
          chatSessionMedias
      );
    }

    emotionService.createEmotionAnalysis(loginUserId, diaryResponse.diaryId());

    return new ChatSessionCloseResponseDto(
        diaryResponse.diaryId(),
        diaryResponse.diaryDate(),
        diaryResponse.contentSummary()
    );
  }

  @Transactional(readOnly = true)
  public List<ChatMessageResponseDto> getChatMessages(Long loginUserId, Long sessionId) {
    if (loginUserId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    ChatSession session = getOwnedSession(loginUserId, sessionId);

    return messageRepository.findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
        .stream()
        .map(ChatMessageResponseDto::from)
        .toList();
  }

  private ChatSession getOwnedSession(Long loginUserId, Long sessionId) {
    userRepository.getByUserId(loginUserId);

    ChatSession session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new ExceptionList(ChatErrorCode.CHAT_SESSION_NOT_FOUND));

    if (!session.getUser().getUserId().equals(loginUserId)) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }

    return session;
  }

  private String getYesterdayDiarySummary(Long userId) {
    LocalDate yesterday = LocalDate.now().minusDays(1);

    Optional<Diary> yesterdayDiary =
        diaryService.findTopDiaryByUserIdAndDiaryDate(userId, yesterday);

    return yesterdayDiary
        .map(Diary::getContentSummary)
        .orElse(null);
  }

  private String generateInitialQuestion(String previousDiarySummary) {
    try {
      String result = chatClient.prompt()
          .messages(List.of(
              new SystemMessage("""
                  당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
                  짧고 부드럽게 말하세요.
                  한 번에 질문은 하나만 하세요.
                  항상 공감부터 하고, 자연스럽게 회상을 유도하세요.
                  전날 기록을 참고해서 오늘 대화를 시작하는 첫 질문을 한 문장으로 생성하세요.
                  """),
              new UserMessage("""
                  아래는 사용자의 전날 일기 요약입니다.
                  이 내용을 바탕으로 오늘 대화를 시작할 회상형 첫 질문을 만들어 주세요.

                  전날 일기 요약:
                  %s
                  """.formatted(previousDiarySummary))
          ))
          .call()
          .content();

      if (result == null || result.isBlank()) {
        return DEFAULT_INITIAL_QUESTION;
      }

      return result;
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
      List<Message> messages = messageRepository
          .findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
          .stream()
          .map(message -> message.getSenderType() == SenderType.USER
              ? new UserMessage(message.getContent())
              : new AssistantMessage(message.getContent()))
          .collect(Collectors.toList());

      messages.add(0, new SystemMessage("""
          당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
          짧고 부드럽게 말하고, 한 번에 질문은 하나만 하세요.
          항상 공감 후 질문하세요.
          """));

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

  private String buildContentStt(List<ChatMessage> messages) {
    return messages.stream()
        .filter(message -> message.getSenderType() == SenderType.USER)
        .map(ChatMessage::getContent)
        .collect(Collectors.joining("\n"));
  }

  private String generateChatSummary(List<ChatMessage> messages) {
    try {
      String fullConversation = messages.stream()
          .map(message -> message.getSenderType() + ": " + message.getContent())
          .collect(Collectors.joining("\n"));

      String result = chatClient.prompt()
          .messages(List.of(
              new SystemMessage("""
                  당신은 경증 치매 어르신의 하루 대화를 요약하는 AI입니다.
                  사용자의 하루를 중심으로 핵심 사건, 감정, 활동을 2~3문장으로 간단히 요약하세요.
                  불필요한 설명 없이 일기 저장용 요약만 작성하세요.
                  """),
              new UserMessage("""
                  아래 대화 내용을 바탕으로 오늘 하루 기록을 요약해 주세요.

                  대화 내용:
                  %s
                  """.formatted(fullConversation))
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

  private ChatMessage saveMessage(ChatSession session, SenderType type, String content) {
    return messageRepository.save(
        ChatMessage.builder()
            .session(session)
            .senderType(type)
            .content(content)
            .build()
    );
  }

}