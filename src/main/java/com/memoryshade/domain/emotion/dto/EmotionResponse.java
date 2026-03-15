package com.memoryshade.domain.emotion.dto;

import java.util.Map;

/**
 * AI 서버(FastAPI)의 감정 분석 응답을 담는 DTO
 * @param top_emotion 가장 높게 분석된 감정 레이블
 * @param confidence 해당 감정의 확신도 (0~100)
 * @param emotions 전체 6가지 감정별 점수 리스트
 */
public record EmotionResponse(
    String top_emotion,
    Double confidence,
    Map<String, Double> emotions
) {}