package com.memoryshade.domain.recall.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecallQuizResultResponseDto(
    Long recallQuizSessionId,
    LocalDate quizDate,
    int totalQuestionCount,
    int correctCount,
    int partialCount,
    double scorePercent,
    boolean isCompleted,
    LocalDateTime completedAt
) {
}