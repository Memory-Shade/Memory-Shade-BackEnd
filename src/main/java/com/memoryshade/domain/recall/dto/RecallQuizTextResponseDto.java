package com.memoryshade.domain.recall.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecallQuizTextResponseDto(
    Long recallQuizSessionId,
    List<RecallQuizMessageResponseDto> messages
) {
}