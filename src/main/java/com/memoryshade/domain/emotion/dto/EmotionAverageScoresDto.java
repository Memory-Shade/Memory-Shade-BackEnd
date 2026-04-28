package com.memoryshade.domain.emotion.dto;

public record EmotionAverageScoresDto(
    int joy,
    int sadness,
    int anger,
    int anxiety,
    int embarrassment,
    int hurt,
    int neutral
) {
  public static EmotionAverageScoresDto zero() {
    return new EmotionAverageScoresDto(0, 0, 0, 0, 0, 0, 0);
  }
}
