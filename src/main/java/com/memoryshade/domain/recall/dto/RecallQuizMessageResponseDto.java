package com.memoryshade.domain.recall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecallQuizMessageResponseDto(
    @JsonProperty("sender_type")
    String senderType,

    String content,

    @JsonProperty("reference_media_url")
    String referenceMediaUrl,

    @JsonProperty("created_at")
    LocalDateTime createdAt
) {
  public static RecallQuizMessageResponseDto ai(String content, String referenceMediaUrl, LocalDateTime createdAt) {
    return new RecallQuizMessageResponseDto(
        "AI",
        content,
        referenceMediaUrl,
        createdAt
    );
  }

  public static RecallQuizMessageResponseDto user(String content, LocalDateTime createdAt) {
    return new RecallQuizMessageResponseDto(
        "USER",
        content,
        null,
        createdAt
    );
  }
}