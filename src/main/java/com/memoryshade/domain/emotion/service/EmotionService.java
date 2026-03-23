package com.memoryshade.domain.emotion.service;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.emotion.dto.EmotionResponseDto;
import com.memoryshade.domain.emotion.model.EmotionType;
import com.memoryshade.domain.emotion.repository.EmotionAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionService {

  private final EmotionAnalysisRepository emotionAnalysisRepository;
  private final DiaryRepository diaryRepository;
  private final RestTemplate restTemplate;

  @Value("${ai.server.url}")
  private String aiServerUrl;

  @Transactional
  public void createEmotionAnalysis(Long loginUserId, Long diaryId) {
    // 1. 일기 존재 여부 및 권한 확인
    Diary diary = diaryRepository.getDiary(diaryId, loginUserId);

    //  이미 분석 결과가 존재한다면 추가 분석을 진행 X
    if (emotionAnalysisRepository.findByDiary(diary).isPresent()) {
      log.info("Diary ID: {} - 이미 분석 결과가 존재하여 분석을 생략합니다.", diaryId);
      return;
    }

    // 분석 결과가 없을 때만 AI 서버 호출 진행
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

  private boolean isReliable(String topEmotion) {
    return !topEmotion.contains(EmotionType.NEUTRAL.getKoreanName()) && !topEmotion.contains("모호함");
  }
}