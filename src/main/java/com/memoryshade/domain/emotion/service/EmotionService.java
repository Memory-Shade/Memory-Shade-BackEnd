package com.memoryshade.domain.emotion.service;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.emotion.dto.EmotionResponse;
import com.memoryshade.domain.emotion.model.EmotionAnalysis;
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
  private final RestTemplate restTemplate;

  @Value("${ai.server.url}")
  private String aiServerUrl;

  /**
   * AI 서버에 분석을 요청하고 결과를 저장
   */
  @Transactional
  public void analyzeAndSave(Diary diary, String text) {
    try {
      // FastAPI AI 서버 호출
      EmotionResponse response = restTemplate.postForObject(
          aiServerUrl,
          Map.of("text", text),
          EmotionResponse.class
      );

      //  필터링 로직: 중립/모호함이 아닐 때만 저장 진행
      if (response != null && isReliable(response.top_emotion())) {
        saveEmotionAnalysis(diary, response);
        log.info("Diary ID: {} - 감정 분석 저장 완료 ({})", diary.getDiaryId(), response.top_emotion());
      } else {
        log.info("Diary ID: {} - 감정이 모호하여 DB 저장을 건너뜀", diary.getDiaryId());
      }
    } catch (Exception e) {
      log.error("AI 감정 분석 중 오류 발생: {}", e.getMessage());
    }
  }

  private boolean isReliable(String topEmotion) {
    return !topEmotion.contains("중립") && !topEmotion.contains("모호함");
  }

  /**
   * AI 응답을 엔티티로 변환하여 DB에 저장합니다.
   * 기존에 동일한 Diary ID로 저장된 데이터가 있다면 삭제 후 갱신합니다.
   */
  private void saveEmotionAnalysis(Diary diary, EmotionResponse res) {
    log.info("AI 응답 데이터: {}", res.emotions()); // 수신 데이터 로그 확인

    emotionAnalysisRepository.findByDiary(diary).ifPresent(existing -> {
      log.info("Diary ID: {}에 대한 기존 분석 결과 삭제 중...", diary.getDiaryId());
      emotionAnalysisRepository.delete(existing);
      emotionAnalysisRepository.flush(); // 즉시 반영하여 중복 제약 조건 충돌 방지
    });

    Map<String, ?> scores = res.emotions();

    EmotionAnalysis analysis = EmotionAnalysis.builder()
        .diary(diary)
        .joyScore(convertToInteger(scores.get("기쁨")))
        .sadnessScore(convertToInteger(scores.get("슬픔")))
        .angerScore(convertToInteger(scores.get("분노")))
        .anxietyScore(convertToInteger(scores.get("불안")))
        .embarrassmentScore(convertToInteger(scores.get("당황")))
        .hurtScore(convertToInteger(scores.get("상처")))
        .build();

    emotionAnalysisRepository.save(analysis);
  }

  /**
   * Object 타입 ->  Integer로 변환.
   */
  private Integer convertToInteger(Object value) {
    if (value == null) return 0;
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    try {
      return Integer.valueOf(value.toString());
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}