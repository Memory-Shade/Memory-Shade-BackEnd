package com.memoryshade.domain.chat.service;

import com.memoryshade.domain.chat.dto.*;
import com.memoryshade.domain.chat.exception.ChatErrorCode;
import com.memoryshade.domain.chat.model.*;
import com.memoryshade.domain.chat.repository.*;
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
import com.memoryshade.global.tts.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

  private static final String DEFAULT_INITIAL_QUESTION = "안녕하세요. 오늘은 어떤 하루를 보내셨나요?";

  private static final String INITIAL_QUESTION_SYSTEM_PROMPT = """
          당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
          짧고 부드럽게 말하세요. 한 번에 질문은 하나만 하세요.
          감정보다 구체적인 사실 회상을 우선하세요.
          식사, 장소, 함께한 사람, 이동, 활동, 본 것, 먹은 것처럼 실제로 있었던 일을 묻는 질문을 하세요.
          감정 질문은 필요할 때만 자연스럽게 덧붙이세요.
          전날 기록을 참고해서 오늘 대화를 시작하는 첫 질문을 한 문장으로 생성하세요.
          예: 오늘 점심에는 어떤 반찬을 드셨나요?
          예: 오늘은 누구와 함께 시간을 보내셨나요?
          예: 오늘 밖에 나가셨다면 어디에 다녀오셨나요?
          """;

  private static final String CHAT_SYSTEM_PROMPT = """
          당신은 경증 치매 어르신의 하루 기록을 도와주는 AI입니다.
          짧고 부드럽게 말하고, 한 번에 질문은 하나만 하세요.
          대화의 목적은 감정 상담보다 하루에 실제로 있었던 일을 구체적으로 기록하는 것입니다.
          우선 식사, 반찬, 장소, 시간대, 함께한 사람, 이동, 활동, 본 것, 들은 것, 산 것 등을 물어보세요.
          감정은 중요하지만 매번 묻지 말고, 사용자가 사건을 말한 뒤 자연스럽게 한 번만 확인하세요.
          질문은 “기분이 어떠셨어요?”보다 “무엇을 드셨어요?”, “어디에 다녀오셨어요?”, “누구와 함께하셨어요?”처럼 사실 중심으로 하세요.
          """;

  private static final String SUMMARY_SYSTEM_PROMPT = """
    당신은 경증 치매 어르신의 하루 대화를 일기 형태로 요약하는 AI입니다.

    반드시 지켜야 할 규칙:
    1. "USER", "사용자", "어르신", "환자", "대상자" 같은 주어 표현을 사용하지 마세요.
    2. 문장의 주어를 억지로 넣지 말고, 하루에 있었던 일을 자연스럽게 요약하세요.
    3. "~하셨습니다"처럼 딱딱한 보고서체보다 "~했어요", "~보냈어요", "~떠올렸어요"처럼 부드러운 일기체로 작성하세요.
    4. 함께한 사람이 있다면 "미영님과 함께", "가족과 함께"처럼 자연스럽게 표현하세요.
    5. 핵심 사건, 장소, 함께한 사람, 활동, 감정을 중심으로 2~3문장으로 요약하세요.
    6. 없는 내용은 추측하지 말고, 대화에 나온 내용만 사용하세요.
    7. 불필요한 설명 없이 일기 저장용 요약만 작성하세요.

    좋은 예:
    - 오늘 미영님과 함께 공원을 산책했어요. 산책 후에는 카페에 들러 커피를 마시며 편안한 시간을 보냈어요.
    - 점심에는 김치찌개와 계란말이를 먹었고, 오후에는 가족과 통화했어요. 하루를 차분하게 보내며 기분이 한결 좋아졌어요.

    나쁜 예:
    - USER님께서는 오늘 미영이와 산책을 하셨습니다.
    - 사용자는 오늘 가족과 시간을 보냈습니다.
    - 어르신은 오늘 기분이 좋았다고 말했습니다.
    """;

  private static final String PHOTO_DESCRIPTION_REQUEST_MESSAGE =
      "사진을 올려주셨네요. 이 사진이 언제, 어디에서 찍은 사진인지 설명해 주실 수 있을까요?";

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
  private final TtsService ttsService;

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

    String initialQuestion = getInitialQuestion(loginUserId);
    ChatMessage initialAiMessage = saveMessage(session, SenderType.AI, initialQuestion);

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
          chatSessionMediaRepository.save(ChatSessionMedia.builder()
              .session(session)
              .mediaUrl(url)
              .mediaType(MediaType.IMAGE)
              .build());
          return url;
        })
        .toList();

    if (mediaUrls.isEmpty()) {
      throw new ExceptionList(ChatErrorCode.EMPTY_IMAGE_FILE);
    }

    saveMessage(session, SenderType.AI, PHOTO_DESCRIPTION_REQUEST_MESSAGE);

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

  @Transactional(readOnly = true)
  public byte[] getChatMessageTts(Long loginUserId, Long sessionId, Long messageId) {
    validateUserId(loginUserId);
    ChatSession session = getOwnedActiveSession(loginUserId, sessionId);

    ChatMessage message = messageRepository.findByMessageIdAndSession_SessionId(
            messageId,
            session.getSessionId()
        )
        .orElseThrow(() -> new ExceptionList(ChatErrorCode.CHAT_MESSAGE_NOT_FOUND));

    if (message.getSenderType() != SenderType.AI) {
      throw new ExceptionList(ChatErrorCode.TTS_NOT_ALLOWED);
    }

    return ttsService.synthesize(message.getContent());
  }

  private void validateUserId(Long userId) {
    if (userId == null) {
      throw new ExceptionList(ChatErrorCode.UNAUTHORIZED_USER);
    }
  }

  private ChatVoiceResponseDto processChatInteraction(ChatSession session, String userText) {
    ChatMessage userMessage = saveMessage(session, SenderType.USER, userText);
    String aiText = generateAiResponse(session);
    ChatMessage aiMessage = saveMessage(session, SenderType.AI, aiText);

    return new ChatVoiceResponseDto(
        session.getSessionId(),
        List.of(ChatMessageResponseDto.from(userMessage), ChatMessageResponseDto.from(aiMessage))
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
      List<Message> messages = new java.util.ArrayList<>(messageRepository
          .findAllBySession_SessionIdOrderByCreatedAtAsc(session.getSessionId())
          .stream()
          .map(m -> m.getSenderType() == SenderType.USER
              ? new UserMessage(m.getContent())
              : new AssistantMessage(m.getContent()))
          .toList());

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

  private String buildContentStt(List<ChatMessage> messages) {
    return messages.stream()
        .filter(m -> m.getSenderType() == SenderType.USER)
        .map(ChatMessage::getContent)
        .collect(java.util.stream.Collectors.joining("\n"));
  }

  private String generateChatSummary(List<ChatMessage> messages) {
    try {
      String conversation = messages.stream()
          .map(m -> m.getSenderType() + ": " + m.getContent())
          .collect(java.util.stream.Collectors.joining("\n"));

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

  private ChatMessage saveMessage(ChatSession session, SenderType type, String content) {
    return messageRepository.save(ChatMessage.builder()
        .session(session)
        .senderType(type)
        .content(content)
        .build());
  }
}