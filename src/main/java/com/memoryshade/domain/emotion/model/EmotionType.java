package com.memoryshade.domain.emotion.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmotionType {
  JOY("기쁨"), SADNESS("슬픔"), ANGER("분노"),
  ANXIETY("불안"), EMBARRASSMENT("당황"), HURT("상처"), NEUTRAL("중립");

  private final String koreanName;

  public static EmotionType fromKoreanName(String name) {
    for (EmotionType type : values()) {
      if (type.koreanName.equals(name)) return type;
    }
    return NEUTRAL;
  }
}