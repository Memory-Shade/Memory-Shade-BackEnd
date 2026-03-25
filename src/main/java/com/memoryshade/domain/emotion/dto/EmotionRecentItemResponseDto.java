package com.memoryshade.domain.emotion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EmotionRecentItemResponseDto(
    @JsonProperty("emotion_type")
    String emotionType,

    @JsonProperty("display_name")
    String displayName,

    @JsonProperty("score")
    Integer score
) {
}