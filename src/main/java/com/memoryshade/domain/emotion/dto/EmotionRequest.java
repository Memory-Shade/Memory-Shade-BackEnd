package com.memoryshade.domain.emotion.dto;

public record EmotionRequest(
    String contentText // 분석할 텍스트
) {}