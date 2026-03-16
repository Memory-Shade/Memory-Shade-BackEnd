package com.memoryshade.domain.emotion.dto;

public record EmotionRequest(
    Long diaryId       // 분석 대상 일기 ID
) {}