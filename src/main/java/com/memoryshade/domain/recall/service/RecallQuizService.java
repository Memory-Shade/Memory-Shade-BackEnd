package com.memoryshade.domain.recall.service;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;
import com.memoryshade.domain.diary.repository.DiaryMediaRepository;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.recall.dto.RecallQuizMessageResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizMessagesReadResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizQuestionCreateDto;
import com.memoryshade.domain.recall.dto.RecallQuizResultResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizSessionCreateResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizTextRequestDto;
import com.memoryshade.domain.recall.dto.RecallQuizTextResponseDto;
import com.memoryshade.domain.recall.exception.RecallErrorCode;
import com.memoryshade.domain.recall.model.RecallQuizAnswer;
import com.memoryshade.domain.recall.model.RecallQuizJudgement;
import com.memoryshade.domain.recall.model.RecallQuizQuestion;
import com.memoryshade.domain.recall.model.RecallQuizSession;
import com.memoryshade.domain.recall.repository.RecallQuizAnswerRepository;
import com.memoryshade.domain.recall.repository.RecallQuizQuestionRepository;
import com.memoryshade.domain.recall.repository.RecallQuizSessionRepository;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecallQuizService {

  private static final int TOTAL_QUESTION_COUNT = 5;
  private static final String COMPLETED_MESSAGE = "회상 질문이 끝났어요. 수고하셨습니다.";

  private static final String QUIZ_EVALUATION_SYSTEM_PROMPT = """
      당신은 경증 치매 어르신의 회상 답변을 평가하는 AI입니다.
      사용자의 답변이 정답과 의미적으로 같으면 CORRECT,
      일부만 맞으면 PARTIAL,
      틀리면 WRONG으로 판단하세요.
      반드시 아래 형식으로만 답하세요.
      judgement: CORRECT | PARTIAL | WRONG
      reason: 한 줄 설명
      """;

  private final RecallQuizSessionRepository recallQuizSessionRepository;
  private final RecallQuizQuestionRepository recallQuizQuestionRepository;
  private final RecallQuizAnswerRepository recallQuizAnswerRepository;
  private final UserRepository userRepository;
  private final DiaryRepository diaryRepository;
  private final DiaryMediaRepository diaryMediaRepository;
  private final ChatClient chatClient;
  private final OpenAiAudioTranscriptionModel sttModel;

  @Transactional
  public RecallQuizSessionCreateResponseDto createRecallQuizSession(Long loginUserId) {
    validateUserId(loginUserId);
    User user = userRepository.getByUserId(loginUserId);
    LocalDate today = LocalDate.now();

    RecallQuizSession session = recallQuizSessionRepository
        .findByUser_UserIdAndQuizDateAndIsCompletedFalse(loginUserId, today)
        .orElseGet(() -> createNewRecallQuizSession(user, loginUserId, today));

    return new RecallQuizSessionCreateResponseDto(
        session.getRecallQuizSessionId(),
        session.getQuizDate(),
        buildMessages(session)
    );
  }

  @Transactional
  public RecallQuizTextResponseDto submitRecallQuizText(
      Long loginUserId,
      Long recallQuizSessionId,
      RecallQuizTextRequestDto request
  ) {
    validateUserId(loginUserId);

    if (request == null || request.content() == null || request.content().isBlank()) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_TEXT_EMPTY);
    }

    return submitRecallQuizAnswer(
        loginUserId,
        recallQuizSessionId,
        request.content().trim()
    );
  }

  @Transactional
  public RecallQuizTextResponseDto submitRecallQuizVoice(
      Long loginUserId,
      Long recallQuizSessionId,
      MultipartFile file
  ) {
    validateUserId(loginUserId);

    if (file == null || file.isEmpty()) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_AUDIO_EMPTY);
    }

    String userText = transcribe(file);
    return submitRecallQuizAnswer(loginUserId, recallQuizSessionId, userText);
  }

  @Transactional(readOnly = true)
  public RecallQuizMessagesReadResponseDto getRecallQuizMessages(Long loginUserId, Long recallQuizSessionId) {
    validateUserId(loginUserId);
    RecallQuizSession session = getOwnedRecallQuizSession(loginUserId, recallQuizSessionId);

    return new RecallQuizMessagesReadResponseDto(buildMessages(session));
  }

  @Transactional(readOnly = true)
  public RecallQuizResultResponseDto getRecallQuizResult(Long loginUserId, Long recallQuizSessionId) {
    validateUserId(loginUserId);
    RecallQuizSession session = getOwnedRecallQuizSession(loginUserId, recallQuizSessionId);

    return new RecallQuizResultResponseDto(
        session.getRecallQuizSessionId(),
        session.getQuizDate(),
        session.getTotalQuestionCount(),
        session.getCorrectCount(),
        session.getPartialCount(),
        session.calculateScorePercent(),
        session.isCompleted(),
        session.getCompletedAt()
    );
  }

  private RecallQuizTextResponseDto submitRecallQuizAnswer(
      Long loginUserId,
      Long recallQuizSessionId,
      String userAnswerText
  ) {
    RecallQuizSession session = getOwnedRecallQuizSession(loginUserId, recallQuizSessionId);

    if (session.isCompleted()) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_ALREADY_COMPLETED);
    }

    RecallQuizQuestion currentQuestion = getCurrentQuestion(session);
    if (currentQuestion == null) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_QUESTION_NOT_FOUND);
    }

    RecallQuizAnswer answer = evaluateAnswer(currentQuestion, userAnswerText);
    session.applyJudgement(answer.getJudgement());

    if (isCompleted(session)) {
      session.complete();

      return new RecallQuizTextResponseDto(
          session.getRecallQuizSessionId(),
          List.of(
              RecallQuizMessageResponseDto.user(answer.getUserAnswer(), answer.getCreatedAt()),
              RecallQuizMessageResponseDto.ai(COMPLETED_MESSAGE, null, session.getCompletedAt())
          )
      );
    }

    RecallQuizQuestion nextQuestion = getNextQuestionToAsk(session);
    if (nextQuestion == null) {
      session.complete();

      return new RecallQuizTextResponseDto(
          session.getRecallQuizSessionId(),
          List.of(
              RecallQuizMessageResponseDto.user(answer.getUserAnswer(), answer.getCreatedAt()),
              RecallQuizMessageResponseDto.ai(COMPLETED_MESSAGE, null, LocalDateTime.now())
          )
      );
    }

    nextQuestion.markAsked();

    return new RecallQuizTextResponseDto(
        session.getRecallQuizSessionId(),
        List.of(
            RecallQuizMessageResponseDto.user(answer.getUserAnswer(), answer.getCreatedAt()),
            RecallQuizMessageResponseDto.ai(
                nextQuestion.getQuestionText(),
                nextQuestion.getReferenceMediaUrl(),
                nextQuestion.getAskedAt()
            )
        )
    );
  }

  private RecallQuizSession createNewRecallQuizSession(User user, Long loginUserId, LocalDate today) {
    List<Diary> recentDiaries = getRecentDiaries(loginUserId);
    if (recentDiaries.isEmpty()) {
      throw new ExceptionList(RecallErrorCode.RECENT_DIARY_NOT_FOUND);
    }

    List<RecallQuizQuestionCreateDto> questionCreateDtos = buildQuestionCreateDtos(recentDiaries);
    if (questionCreateDtos.isEmpty()) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_CREATE_FAILED);
    }

    RecallQuizSession session = recallQuizSessionRepository.save(
        RecallQuizSession.builder()
            .user(user)
            .quizDate(today)
            .totalQuestionCount(questionCreateDtos.size())
            .build()
    );

    List<RecallQuizQuestion> questions = recallQuizQuestionRepository.saveAll(
        questionCreateDtos.stream()
            .map(dto -> RecallQuizQuestion.builder()
                .recallQuizSession(session)
                .questionOrder(dto.questionOrder())
                .questionText(dto.questionText())
                .expectedAnswer(dto.expectedAnswer())
                .sourceDiary(dto.sourceDiary())
                .sourceDiaryMedia(dto.sourceDiaryMedia())
                .build())
            .toList()
    );

    if (questions.isEmpty()) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_CREATE_FAILED);
    }

    questions.get(0).markAsked();
    return session;
  }

  private RecallQuizSession getOwnedRecallQuizSession(Long loginUserId, Long recallQuizSessionId) {
    userRepository.getByUserId(loginUserId);

    RecallQuizSession session = recallQuizSessionRepository.findById(recallQuizSessionId)
        .orElseThrow(() -> new ExceptionList(RecallErrorCode.RECALL_QUIZ_SESSION_NOT_FOUND));

    if (!session.getUser().getUserId().equals(loginUserId)) {
      throw new ExceptionList(RecallErrorCode.UNAUTHORIZED_USER);
    }

    return session;
  }

  private RecallQuizQuestion getCurrentQuestion(RecallQuizSession session) {
    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(session.getRecallQuizSessionId());

    for (RecallQuizQuestion question : questions) {
      boolean answered = recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(
          question.getRecallQuizQuestionId()
      );

      if (question.isAsked() && !answered) {
        return question;
      }
    }

    return null;
  }

  private RecallQuizQuestion getNextQuestionToAsk(RecallQuizSession session) {
    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(session.getRecallQuizSessionId());

    for (RecallQuizQuestion question : questions) {
      if (!question.isAsked()) {
        return question;
      }
    }

    return null;
  }

  private boolean isCompleted(RecallQuizSession session) {
    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(session.getRecallQuizSessionId());

    long answeredCount = questions.stream()
        .filter(question -> recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(
            question.getRecallQuizQuestionId()
        ))
        .count();

    return answeredCount >= questions.size();
  }

  private RecallQuizAnswer evaluateAnswer(RecallQuizQuestion question, String userAnswer) {
    try {
      String result = chatClient.prompt()
          .messages(List.of(
              new SystemMessage(QUIZ_EVALUATION_SYSTEM_PROMPT),
              new UserMessage("""
                  문제: %s
                  정답 기준: %s
                  사용자 답변: %s
                  """.formatted(
                  question.getQuestionText(),
                  question.getExpectedAnswer(),
                  userAnswer
              ))
          ))
          .call()
          .content();

      RecallQuizJudgement judgement = parseJudgement(result);
      String reason = parseReason(result);

      return recallQuizAnswerRepository.save(
          RecallQuizAnswer.builder()
              .recallQuizQuestion(question)
              .userAnswer(userAnswer)
              .judgement(judgement)
              .evaluationReason(reason)
              .build()
      );
    } catch (Exception e) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_EVALUATION_FAILED);
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
        throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_STT_FAILED);
      }

      return result;
    } catch (Exception e) {
      throw new ExceptionList(RecallErrorCode.RECALL_QUIZ_STT_FAILED);
    }
  }

  private List<RecallQuizMessageResponseDto> buildMessages(RecallQuizSession session) {
    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(session.getRecallQuizSessionId());

    List<RecallQuizMessageResponseDto> messages = new ArrayList<>();

    for (RecallQuizQuestion question : questions) {
      if (!question.isAsked()) {
        break;
      }

      messages.add(
          RecallQuizMessageResponseDto.ai(
              question.getQuestionText(),
              question.getReferenceMediaUrl(),
              question.getAskedAt()
          )
      );

      RecallQuizAnswer answer = recallQuizAnswerRepository
          .findByRecallQuizQuestion_RecallQuizQuestionId(question.getRecallQuizQuestionId())
          .orElse(null);

      if (answer == null) {
        break;
      }

      messages.add(
          RecallQuizMessageResponseDto.user(
              answer.getUserAnswer(),
              answer.getCreatedAt()
          )
      );
    }

    if (session.isCompleted()) {
      messages.add(
          RecallQuizMessageResponseDto.ai(
              COMPLETED_MESSAGE,
              null,
              session.getCompletedAt()
          )
      );
    }

    return messages;
  }

  private void validateUserId(Long userId) {
    if (userId == null) {
      throw new ExceptionList(RecallErrorCode.UNAUTHORIZED_USER);
    }
  }

  private List<Diary> getRecentDiaries(Long loginUserId) {
    LocalDate endDate = LocalDate.now().minusDays(1);
    LocalDate startDate = endDate.minusDays(6);

    return diaryRepository.findAllByUser_UserIdAndDiaryDateBetweenOrderByDiaryDateDesc(
        loginUserId,
        startDate,
        endDate
    );
  }

  private List<RecallQuizQuestionCreateDto> buildQuestionCreateDtos(List<Diary> recentDiaries) {
    List<RecallQuizQuestionCreateDto> results = new ArrayList<>();

    if (recentDiaries.isEmpty()) {
      return results;
    }

    int questionOrder = 1;
    int round = 0;

    while (questionOrder <= TOTAL_QUESTION_COUNT) {
      Diary diary = recentDiaries.get(round % recentDiaries.size());

      List<DiaryMedia> diaryMedias = diaryMediaRepository.findAllByDiary_DiaryId(diary.getDiaryId());
      DiaryMedia firstMedia = diaryMedias.isEmpty() ? null : diaryMedias.get(0);

      String questionText = buildQuestionText(diary, firstMedia, questionOrder);
      String expectedAnswer = buildExpectedAnswer(diary);

      results.add(new RecallQuizQuestionCreateDto(
          questionOrder,
          questionText,
          expectedAnswer,
          diary,
          firstMedia
      ));

      questionOrder++;
      round++;
    }

    return results;
  }

  private String buildQuestionText(Diary diary, DiaryMedia diaryMedia, int questionOrder) {
    String summary = diary.getContentSummary() == null ? "" : diary.getContentSummary();

    if (diaryMedia != null) {
      return buildPhotoBasedQuestion(summary, diary.getDiaryDate(), questionOrder);
    }

    return buildTextBasedQuestion(summary, diary.getDiaryDate(), questionOrder);
  }

  private String buildPhotoBasedQuestion(String summary, LocalDate diaryDate, int questionOrder) {
    return switch (questionOrder % 5) {
      case 1 -> "이 사진을 보면 그날의 기억이 조금 떠오르실 수 있어요. 이때 어디에 계셨는지 기억나시나요?";
      case 2 -> "사진 속 하루를 함께 떠올려 볼게요. 이 날 누구와 함께 시간을 보내셨나요?";
      case 3 -> "이 사진은 %s의 기록이에요. 이때 무엇을 하고 계셨는지 기억나시나요?".formatted(diaryDate);
      case 4 -> "사진을 천천히 보시고, 이 날 있었던 일을 한 가지 말씀해 주실 수 있을까요?";
      default -> "이 사진을 찍었던 날을 떠올려 볼게요. 어떤 장소에서 어떤 일을 하셨나요?";
    };
  }

  private String buildTextBasedQuestion(String summary, LocalDate diaryDate, int questionOrder) {
    if (summary.contains("카페") || summary.contains("커피")) {
      return switch (questionOrder % 3) {
        case 1 -> "최근 카페에 다녀오신 기록이 있어요. 누구와 함께 가셨는지 기억나시나요?";
        case 2 -> "카페에 가셨던 날을 떠올려 볼게요. 그때 무엇을 드셨나요?";
        default -> "카페에 다녀오신 날, 어디에서 시간을 보내셨는지 기억나시나요?";
      };
    }

    if (summary.contains("병원") || summary.contains("검진")) {
      return switch (questionOrder % 3) {
        case 1 -> "병원에 다녀오신 날이 있었어요. 어떤 일로 병원에 가셨는지 기억나시나요?";
        case 2 -> "그날 병원에 가기 전이나 후에 무엇을 하셨는지 떠오르시나요?";
        default -> "병원에 다녀오신 기록을 떠올려 볼게요. 누구와 함께 가셨나요?";
      };
    }

    if (summary.contains("시장") || summary.contains("마트") || summary.contains("장")) {
      return switch (questionOrder % 3) {
        case 1 -> "장을 보러 다녀오신 날이 있었어요. 어디에 가셨는지 기억나시나요?";
        case 2 -> "그날 장을 보면서 무엇을 사셨는지 떠오르시나요?";
        default -> "시장이나 마트에 다녀오신 날, 누구와 함께 가셨나요?";
      };
    }

    if (summary.contains("공원") || summary.contains("산책")) {
      return switch (questionOrder % 3) {
        case 1 -> "산책이나 공원에 다녀오신 기록이 있어요. 어디를 걸으셨는지 기억나시나요?";
        case 2 -> "그날 산책할 때 누구와 함께하셨나요?";
        default -> "공원에 다녀오신 날, 무엇을 하셨는지 떠오르시나요?";
      };
    }

    if (summary.contains("가족") || summary.contains("손자") || summary.contains("아들")) {
      return switch (questionOrder % 3) {
        case 1 -> "가족과 함께한 기록이 있어요. 그날 누구와 함께 계셨나요?";
        case 2 -> "가족과 시간을 보내신 날, 무엇을 하셨는지 기억나시나요?";
        default -> "그날 가족과 어디에 계셨는지 떠오르시나요?";
      };
    }

    return switch (questionOrder % 4) {
      case 1 -> "%s의 기록을 떠올려 볼게요. 그날 어디에 계셨나요?".formatted(diaryDate);
      case 2 -> "%s에 있었던 일을 생각해 볼게요. 누구와 함께하셨나요?".formatted(diaryDate);
      case 3 -> "%s의 하루 중 기억나는 활동이 있으신가요?".formatted(diaryDate);
      default -> "%s의 기록에서 떠오르는 일을 한 가지 말씀해 주실 수 있을까요?".formatted(diaryDate);
    };
  }

  private String buildExpectedAnswer(Diary diary) {
    if (diary.getContentSummary() != null && !diary.getContentSummary().isBlank()) {
      return diary.getContentSummary();
    }

    return diary.getContentStt() == null ? "" : diary.getContentStt();
  }

  private RecallQuizJudgement parseJudgement(String result) {
    if (result != null && result.contains("CORRECT")) {
      return RecallQuizJudgement.CORRECT;
    }

    if (result != null && result.contains("PARTIAL")) {
      return RecallQuizJudgement.PARTIAL;
    }

    return RecallQuizJudgement.WRONG;
  }

  private String parseReason(String result) {
    if (result == null || result.isBlank()) {
      return "";
    }

    String[] lines = result.split("\n");
    for (String line : lines) {
      if (line.startsWith("reason:")) {
        return line.replace("reason:", "").trim();
      }
    }

    return "";
  }
}