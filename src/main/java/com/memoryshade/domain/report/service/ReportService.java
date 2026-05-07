package com.memoryshade.domain.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoryshade.domain.emotion.dto.EmotionMonthlyAverageComparisonResponseDto;
import com.memoryshade.domain.emotion.service.EmotionService;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.recall.dto.RecallQuizWeeklyAverageComparisonResponseDto;
import com.memoryshade.domain.recall.service.RecallQuizService;
import com.memoryshade.domain.report.dto.StatusSummaryResponseDto;
import com.memoryshade.domain.report.exception.ReportErrorCode;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

  private static final String DEFAULT_STATUS_SUMMARY =
      "현재 제공된 기록을 기준으로 상태를 요약하기 어렵습니다. 회상 퀴즈와 감정 분석 기록이 더 쌓이면 보호자가 참고할 수 있는 요약을 제공할 수 있습니다.";

  private static final String STATUS_SUMMARY_SYSTEM_PROMPT = """
      당신은 보호자에게 사용자의 최근 상태를 설명하는 상태보고서 요약 도우미입니다.

      반드시 지켜야 할 규칙:
      1. 의학적 진단을 하지 마세요.
      2. 위험, 악화, 치매 진행, 이상 징후 같은 단정적 표현을 사용하지 마세요.
      3. 보호자가 직접 위험 여부를 판단할 수 있도록, 제공된 수치의 변화만 부드럽게 요약하세요.
      4. 제공된 데이터에 없는 내용을 추측하지 마세요.
      5. 회상 퀴즈와 감정 분석 결과를 중심으로 2~4문장으로 요약하세요.
      6. 존댓말을 사용하세요.
      7. 마지막 문장은 보호자가 참고할 수 있는 관찰 포인트로 마무리하세요.

      좋은 예시:
      - 회상 퀴즈 정답률은 이전 기간과 비슷한 수준을 유지하고 있습니다.
      - 감정 분석에서는 행복 점수가 지난달보다 높게 나타났습니다.
      - 보호자는 최근 활동이나 대화에서 평소와 다른 변화가 있는지 함께 확인해볼 수 있습니다.

      나쁜 예시:
      - 상태가 위험합니다.
      - 치매가 악화된 것으로 보입니다.
      - 즉시 병원에 가야 합니다.
      """;

  private final EmotionService emotionService;
  private final RecallQuizService recallQuizService;
  private final ChatClient chatClient;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final GuardianLinkRepository guardianLinkRepository;

  @Transactional(readOnly = true)
  public StatusSummaryResponseDto getStatusSummary(Long loginUserId, Long userId) {
    validateGuardianCanReadStatusSummary(loginUserId, userId);

    EmotionMonthlyAverageComparisonResponseDto emotionComparison =
        emotionService.getMonthlyEmotionAverageComparison(loginUserId, userId);

    RecallQuizWeeklyAverageComparisonResponseDto recallComparison =
        recallQuizService.getWeeklyRecallQuizAverageComparison(loginUserId, userId);

    String summary = generateStatusSummary(emotionComparison, recallComparison);

    return new StatusSummaryResponseDto(
        summary,
        LocalDateTime.now()
    );
  }

  private String generateStatusSummary(
      EmotionMonthlyAverageComparisonResponseDto emotionComparison,
      RecallQuizWeeklyAverageComparisonResponseDto recallComparison
  ) {
    try {
      String emotionJson = toJson(emotionComparison);
      String recallJson = toJson(recallComparison);

      String result = chatClient.prompt()
          .messages(List.of(
              new SystemMessage(STATUS_SUMMARY_SYSTEM_PROMPT),
              new UserMessage("""
                  아래 데이터는 보호자 상태보고서에 사용되는 지표입니다.

                  [회상 퀴즈 정답률 비교 데이터]
                  %s

                  [감정 분석 월간 평균 비교 데이터]
                  %s

                  위 데이터를 바탕으로 보호자에게 보여줄 상태 종합 요약문을 작성하세요.
                  위험 여부 판단은 하지 말고, 수치 변화와 관찰 포인트만 요약하세요.
                  """.formatted(recallJson, emotionJson))
          ))
          .call()
          .content();

      if (result == null || result.isBlank()) {
        return DEFAULT_STATUS_SUMMARY;
      }

      return result.trim();
    } catch (Exception e) {
      throw new ExceptionList(ReportErrorCode.STATUS_SUMMARY_GENERATION_FAILED);
    }
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new ExceptionList(ReportErrorCode.STATUS_SUMMARY_GENERATION_FAILED);
    }
  }

  private void validateGuardianCanReadStatusSummary(Long loginUserId, Long userId) {
    if (loginUserId == null) {
      throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
    }

    User loginUser = userRepository.getByUserId(loginUserId);
    User targetUser = userRepository.getByUserId(userId);

    if (loginUser.getRole() != Role.GUARDIAN) {
      throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
    }

    if (targetUser.getRole() != Role.USER) {
      throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
    }

    guardianLinkRepository.validateLinked(userId, loginUserId);
  }
}