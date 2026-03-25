package com.memoryshade.domain.emotion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EmotionRecentReadResponseDto(
    @JsonProperty("top_emotions")
    List<EmotionRecentItemResponseDto> topEmotions
) {
}