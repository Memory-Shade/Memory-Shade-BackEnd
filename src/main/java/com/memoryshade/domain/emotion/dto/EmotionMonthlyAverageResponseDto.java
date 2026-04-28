package com.memoryshade.domain.emotion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record EmotionMonthlyAverageResponseDto(
    @JsonProperty("year_month")
    String yearMonth,

    @JsonProperty("start_date")
    LocalDate startDate,

    @JsonProperty("end_date")
    LocalDate endDate,

    @JsonProperty("record_count")
    int recordCount,

    @JsonProperty("has_data")
    boolean hasData,

    EmotionAverageScoresDto averages
) {
}
