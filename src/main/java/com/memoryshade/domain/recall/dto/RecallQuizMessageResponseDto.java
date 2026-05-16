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
    LocalDateTime createdAt,

    @JsonProperty("tts_url")
    String ttsUrl
) {
  public static RecallQuizMessageResponseDto ai(
      Long recallQuizSessionId,
      Long recallQuizQuestionId,
      String content,
      String referenceMediaUrl,
      LocalDateTime createdAt
  ) {
    return new RecallQuizMessageResponseDto(
        "AI",
        content,
        referenceMediaUrl,
        createdAt,
        buildTtsUrl(recallQuizSessionId, recallQuizQuestionId)
    );
  }

  public static RecallQuizMessageResponseDto aiWithoutTts(
      String content,
      String referenceMediaUrl,
      LocalDateTime createdAt
  ) {
    return new RecallQuizMessageResponseDto(
        "AI",
        content,
        referenceMediaUrl,
        createdAt,
        null
    );
  }

  public static RecallQuizMessageResponseDto user(
      String content,
      LocalDateTime createdAt
  ) {
    return new RecallQuizMessageResponseDto(
        "USER",
        content,
        null,
        createdAt,
        null
    );
  }

  private static String buildTtsUrl(Long recallQuizSessionId, Long recallQuizQuestionId) {
    if (recallQuizSessionId == null || recallQuizQuestionId == null) {
      return null;
    }

    return "/api/recall-quiz-sessions/%d/questions/%d/tts".formatted(
        recallQuizSessionId,
        recallQuizQuestionId
    );
  }
}