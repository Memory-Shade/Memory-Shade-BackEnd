package com.memoryshade.domain.emotion.service;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.emotion.dto.EmotionRecentItemResponseDto;
import com.memoryshade.domain.emotion.dto.EmotionRecentReadResponseDto;
import com.memoryshade.domain.emotion.dto.EmotionResponseDto;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
import com.memoryshade.domain.emotion.model.EmotionType;
import com.memoryshade.domain.emotion.repository.EmotionAnalysisRepository;
import com.memoryshade.domain.guardianLink.exception.GuardianLinkErrorCode;
import com.memoryshade.domain.guardianLink.repository.GuardianLinkRepository;
import com.memoryshade.domain.user.model.Role;
import com.memoryshade.domain.user.model.User;
import com.memoryshade.domain.user.repository.UserRepository;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionService {

  private final EmotionAnalysisRepository emotionAnalysisRepository;
  private final DiaryRepository diaryRepository;
  private final UserRepository userRepository;
  private final GuardianLinkRepository guardianLinkRepository;
  private final RestTemplate restTemplate;

  @Value("${ai.server.url}")
  private String aiServerUrl;

  @Transactional
  public void createEmotionAnalysis(Long loginUserId, Long diaryId) {
    Diary diary = diaryRepository.getDiary(diaryId, loginUserId);

    if (emotionAnalysisRepository.findByDiary(diary).isPresent()) {
      log.info("Diary ID: {} - 이미 분석 결과가 존재하여 분석을 생략합니다.", diaryId);
      return;
    }

    try {
      EmotionResponseDto response = restTemplate.postForObject(
          aiServerUrl,
          Map.of("text", diary.getContentStt()),
          EmotionResponseDto.class
      );

      if (response != null && isReliable(response.topEmotion())) {
        emotionAnalysisRepository.save(response.toEntity(diary));
        log.info("Diary ID: {} - 감정 분석 완료 및 저장되었습니다.", diaryId);
      }
    } catch (Exception e) {
      log.error("AI 감정 분석 중 통신 오류 발생: {}", e.getMessage());
    }
  }

  @Transactional(readOnly = true)
  public EmotionRecentReadResponseDto getRecentEmotionSummary(
      Long loginGuardianId,
      Long userId
  ) {
    if (loginGuardianId == null) {
      throw new ExceptionList(GuardianLinkErrorCode.UNAUTHORIZED_GUARDIAN);
    }

    User guardian = userRepository.getByUserId(loginGuardianId);
    if (guardian.getRole() != Role.GUARDIAN) {
      throw new ExceptionList(GuardianLinkErrorCode.GUARDIAN_ONLY);
    }

    User targetUser = userRepository.getByUserId(userId);
    if (targetUser.getRole() != Role.USER) {
      throw new ExceptionList(GuardianLinkErrorCode.TARGET_USER_ONLY);
    }

    guardianLinkRepository.validateLinked(userId, loginGuardianId);

    LocalDate endDate = LocalDate.now();
    LocalDate startDate = endDate.minusDays(29);

    List<EmotionAnalysis> analyses =
        emotionAnalysisRepository.findAllByDiary_User_UserIdAndDiary_DiaryDateBetween(
            userId,
            startDate,
            endDate
        );

    if (analyses.isEmpty()) {
      return new EmotionRecentReadResponseDto(List.of());
    }

    int count = analyses.size();

    List<EmotionRecentItemResponseDto> topEmotions = List.of(
            new EmotionRecentItemResponseDto(
                EmotionType.JOY.name(),
                "행복",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getJoyScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.SADNESS.name(),
                "슬픔",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getSadnessScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.ANGER.name(),
                "분노",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getAngerScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.ANXIETY.name(),
                "불안",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getAnxietyScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.EMBARRASSMENT.name(),
                "당황",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getEmbarrassmentScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.HURT.name(),
                "상처",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getHurtScore).sum() / count)
            ),
            new EmotionRecentItemResponseDto(
                EmotionType.NEUTRAL.name(),
                "여유",
                Math.round((float) analyses.stream().mapToInt(EmotionAnalysis::getNeutralScore).sum() / count)
            )
        ).stream()
        .sorted(Comparator.comparingInt(EmotionRecentItemResponseDto::score).reversed())
        .limit(3)
        .toList();

    return new EmotionRecentReadResponseDto(topEmotions);
  }

  private boolean isReliable(String topEmotion) {
    return !topEmotion.contains(EmotionType.NEUTRAL.getKoreanName()) && !topEmotion.contains("모호함");
  }
}