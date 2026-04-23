//package com.memoryshade.domain.recall.service;
//
//import com.memoryshade.domain.chat.model.ChatSession;
//import com.memoryshade.domain.diary.model.Diary;
//import com.memoryshade.domain.diary.model.DiaryMedia;
//import com.memoryshade.domain.recall.dto.RecallQuizQuestionCreateDto;
//import com.memoryshade.domain.recall.model.RecallQuizAnswer;
//import com.memoryshade.domain.recall.model.RecallQuizJudgement;
//import com.memoryshade.domain.recall.model.RecallQuizQuestion;
//import com.memoryshade.domain.recall.model.RecallQuizSession;
//import com.memoryshade.domain.recall.repository.RecallQuizAnswerRepository;
//import com.memoryshade.domain.recall.repository.RecallQuizQuestionRepository;
//import com.memoryshade.domain.recall.repository.RecallQuizSessionRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.messages.SystemMessage;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class RecallQuizService {
//
//  private static final int TOTAL_QUESTION_COUNT = 5;
//
//  private static final String QUIZ_EVALUATION_SYSTEM_PROMPT = """
//        당신은 경증 치매 어르신의 회상 답변을 평가하는 AI입니다.
//        사용자의 답변이 정답과 의미적으로 같으면 CORRECT,
//        일부만 맞으면 PARTIAL,
//        틀리면 WRONG으로 판단하세요.
//        반드시 아래 형식으로만 답하세요.
//        judgement: CORRECT | PARTIAL | WRONG
//        reason: 한 줄 설명
//        """;
//
//  private final RecallQuizSessionRepository recallQuizSessionRepository;
//  private final RecallQuizQuestionRepository recallQuizQuestionRepository;
//  private final RecallQuizAnswerRepository recallQuizAnswerRepository;
//  private final ChatClient chatClient;
//
//  @Transactional
//  public RecallQuizSession createRecallQuizSession(
//      ChatSession session,
//      List<RecallQuizQuestionCreateDto> questionCreateDtos
//  ) {
//    RecallQuizSession recallQuizSession = recallQuizSessionRepository.save(
//        RecallQuizSession.builder()
//            .session(session)
//            .totalQuestionCount(questionCreateDtos.size())
//            .build()
//    );
//
//    List<RecallQuizQuestion> questions = questionCreateDtos.stream()
//        .map(dto -> RecallQuizQuestion.builder()
//            .recallQuizSession(recallQuizSession)
//            .questionOrder(dto.questionOrder())
//            .questionText(dto.questionText())
//            .expectedAnswer(dto.expectedAnswer())
//            .sourceDiary(dto.sourceDiary())
//            .sourceDiaryMedia(dto.sourceDiaryMedia())
//            .build())
//        .toList();
//
//    recallQuizQuestionRepository.saveAll(questions);
//    return recallQuizSession;
//  }
//
//  @Transactional(readOnly = true)
//  public RecallQuizQuestion getFirstQuestion(Long sessionId) {
//    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
//        .orElseThrow();
//    return recallQuizQuestionRepository
//        .findByRecallQuizSession_RecallQuizSessionIdAndQuestionOrder(recallQuizSession.getRecallQuizSessionId(), 1)
//        .orElseThrow();
//  }
//
//  @Transactional(readOnly = true)
//  public RecallQuizQuestion getCurrentQuestion(Long sessionId) {
//    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
//        .orElseThrow();
//
//    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
//        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(recallQuizSession.getRecallQuizSessionId());
//
//    for (RecallQuizQuestion question : questions) {
//      if (!recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(question.getRecallQuizQuestionId())) {
//        return question;
//      }
//    }
//
//    return null;
//  }
//
//  @Transactional
//  public RecallQuizAnswer evaluateAnswer(RecallQuizQuestion question, String userAnswer) {
//    String result = chatClient.prompt()
//        .messages(List.of(
//            new SystemMessage(QUIZ_EVALUATION_SYSTEM_PROMPT),
//            new UserMessage("""
//                    문제: %s
//                    정답 기준: %s
//                    사용자 답변: %s
//                    """.formatted(
//                question.getQuestionText(),
//                question.getExpectedAnswer(),
//                userAnswer
//            ))
//        ))
//        .call()
//        .content();
//
//    RecallQuizJudgement judgement = parseJudgement(result);
//    String reason = parseReason(result);
//
//    return recallQuizAnswerRepository.save(
//        RecallQuizAnswer.builder()
//            .recallQuizQuestion(question)
//            .userAnswer(userAnswer)
//            .judgement(judgement)
//            .evaluationReason(reason)
//            .build()
//    );
//  }
//
//  @Transactional
//  public void applyResult(Long sessionId, RecallQuizJudgement judgement) {
//    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
//        .orElseThrow();
//
//    recallQuizSession.applyJudgement(judgement);
//
//    if (isCompleted(sessionId)) {
//      recallQuizSession.complete();
//    }
//  }
//
//  @Transactional(readOnly = true)
//  public boolean hasRecallQuiz(Long sessionId) {
//    return recallQuizSessionRepository.existsBySession_SessionId(sessionId);
//  }
//
//  @Transactional(readOnly = true)
//  public boolean isCompleted(Long sessionId) {
//    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
//        .orElseThrow();
//
//    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
//        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(recallQuizSession.getRecallQuizSessionId());
//
//    long answeredCount = questions.stream()
//        .filter(question -> recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(question.getRecallQuizQuestionId()))
//        .count();
//
//    return answeredCount >= questions.size();
//  }
//
//  public int getTotalQuestionCount() {
//    return TOTAL_QUESTION_COUNT;
//  }
//
//  private RecallQuizJudgement parseJudgement(String result) {
//    if (result != null && result.contains("CORRECT")) {
//      return RecallQuizJudgement.CORRECT;
//    }
//    if (result != null && result.contains("PARTIAL")) {
//      return RecallQuizJudgement.PARTIAL;
//    }
//    return RecallQuizJudgement.WRONG;
//  }
//
//  private String parseReason(String result) {
//    if (result == null || result.isBlank()) {
//      return "";
//    }
//
//    String[] lines = result.split("\n");
//    for (String line : lines) {
//      if (line.startsWith("reason:")) {
//        return line.replace("reason:", "").trim();
//      }
//    }
//    return "";
//  }
//}


package com.memoryshade.domain.recall.service;

import com.memoryshade.domain.chat.model.ChatSession;
import com.memoryshade.domain.chat.repository.ChatSessionRepository;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.model.DiaryMedia;
import com.memoryshade.domain.diary.repository.DiaryMediaRepository;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.recall.dto.RecallQuizQuestionCreateDto;
import com.memoryshade.domain.recall.model.RecallQuizAnswer;
import com.memoryshade.domain.recall.model.RecallQuizJudgement;
import com.memoryshade.domain.recall.model.RecallQuizQuestion;
import com.memoryshade.domain.recall.model.RecallQuizSession;
import com.memoryshade.domain.recall.repository.RecallQuizAnswerRepository;
import com.memoryshade.domain.recall.repository.RecallQuizQuestionRepository;
import com.memoryshade.domain.recall.repository.RecallQuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecallQuizService {

  private static final int TOTAL_QUESTION_COUNT = 5;

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
  private final ChatSessionRepository chatSessionRepository;
  private final DiaryRepository diaryRepository;
  private final DiaryMediaRepository diaryMediaRepository;
  private final ChatClient chatClient;

  @Transactional
  public void createRecallQuiz(Long loginUserId, Long sessionId) {
    ChatSession session = chatSessionRepository.findById(sessionId)
        .orElseThrow();

    List<Diary> recentDiaries = getRecentDiaries(loginUserId);
    List<RecallQuizQuestionCreateDto> questionCreateDtos = buildQuestionCreateDtos(recentDiaries);

    if (questionCreateDtos.isEmpty()) {
      return;
    }

    createRecallQuizSession(session, questionCreateDtos);
  }

  @Transactional
  public RecallQuizSession createRecallQuizSession(
      ChatSession session,
      List<RecallQuizQuestionCreateDto> questionCreateDtos
  ) {
    RecallQuizSession recallQuizSession = recallQuizSessionRepository.save(
        RecallQuizSession.builder()
            .session(session)
            .totalQuestionCount(questionCreateDtos.size())
            .build()
    );

    List<RecallQuizQuestion> questions = questionCreateDtos.stream()
        .map(dto -> RecallQuizQuestion.builder()
            .recallQuizSession(recallQuizSession)
            .questionOrder(dto.questionOrder())
            .questionText(dto.questionText())
            .expectedAnswer(dto.expectedAnswer())
            .sourceDiary(dto.sourceDiary())
            .sourceDiaryMedia(dto.sourceDiaryMedia())
            .build())
        .toList();

    recallQuizQuestionRepository.saveAll(questions);
    return recallQuizSession;
  }

  @Transactional(readOnly = true)
  public RecallQuizQuestion getFirstQuestion(Long sessionId) {
    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
        .orElseThrow();

    return recallQuizQuestionRepository
        .findByRecallQuizSession_RecallQuizSessionIdAndQuestionOrder(
            recallQuizSession.getRecallQuizSessionId(),
            1
        )
        .orElseThrow();
  }

  @Transactional(readOnly = true)
  public RecallQuizQuestion getCurrentQuestion(Long sessionId) {
    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
        .orElseThrow();

    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(
            recallQuizSession.getRecallQuizSessionId()
        );

    for (RecallQuizQuestion question : questions) {
      if (!recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(
          question.getRecallQuizQuestionId()
      )) {
        return question;
      }
    }

    return null;
  }

  @Transactional
  public RecallQuizAnswer evaluateAnswer(RecallQuizQuestion question, String userAnswer) {
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
  }

  @Transactional
  public void applyResult(Long sessionId, RecallQuizJudgement judgement) {
    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
        .orElseThrow();

    recallQuizSession.applyJudgement(judgement);

    if (isCompleted(sessionId)) {
      recallQuizSession.complete();
    }
  }

  @Transactional(readOnly = true)
  public boolean hasRecallQuiz(Long sessionId) {
    return recallQuizSessionRepository.existsBySession_SessionId(sessionId);
  }

  @Transactional(readOnly = true)
  public boolean isCompleted(Long sessionId) {
    RecallQuizSession recallQuizSession = recallQuizSessionRepository.findBySession_SessionId(sessionId)
        .orElseThrow();

    List<RecallQuizQuestion> questions = recallQuizQuestionRepository
        .findAllByRecallQuizSession_RecallQuizSessionIdOrderByQuestionOrderAsc(
            recallQuizSession.getRecallQuizSessionId()
        );

    long answeredCount = questions.stream()
        .filter(question -> recallQuizAnswerRepository.existsByRecallQuizQuestion_RecallQuizQuestionId(
            question.getRecallQuizQuestionId()
        ))
        .count();

    return answeredCount >= questions.size();
  }

  public int getTotalQuestionCount() {
    return TOTAL_QUESTION_COUNT;
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
    int questionOrder = 1;

    for (Diary diary : recentDiaries) {
      if (questionOrder > TOTAL_QUESTION_COUNT) {
        break;
      }

      List<DiaryMedia> diaryMedias = diaryMediaRepository.findAllByDiary_DiaryId(diary.getDiaryId());
      DiaryMedia firstMedia = diaryMedias.isEmpty() ? null : diaryMedias.get(0);

      String questionText = buildQuestionText(diary, firstMedia);
      String expectedAnswer = buildExpectedAnswer(diary);

      results.add(new RecallQuizQuestionCreateDto(
          questionOrder,
          questionText,
          expectedAnswer,
          diary,
          firstMedia
      ));

      questionOrder++;
    }

    return results;
  }

  private String buildQuestionText(Diary diary, DiaryMedia diaryMedia) {
    if (diaryMedia != null) {
      return "이 사진은 최근 기록에 있던 사진이에요. 이때 누구와 무엇을 하셨는지 기억나시나요?";
    }
    return "최근 하루 기록을 떠올려 볼게요. 그날 어떤 일을 하셨는지 기억나시나요?";
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