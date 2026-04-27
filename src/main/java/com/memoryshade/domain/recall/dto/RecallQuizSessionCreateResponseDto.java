package com.memoryshade.domain.recall.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecallQuizSessionCreateResponseDto(
    Long recallQuizSessionId,
    LocalDate quizDate,
    List<RecallQuizMessageResponseDto> messages
) {
}