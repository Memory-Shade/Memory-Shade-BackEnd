package com.memoryshade.domain.emotion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmotionMonthlyAverageComparisonResponseDto(
    @JsonProperty("current_month")
    EmotionMonthlyAverageResponseDto currentMonth,

    @JsonProperty("previous_month")
    EmotionMonthlyAverageResponseDto previousMonth
) {
}
