package com.memoryshade.domain.step.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record WeeklyStepStatsResponseDto(

        @JsonProperty("start_date")
        LocalDate startDate,

        @JsonProperty("end_date")
        LocalDate endDate,

        @JsonProperty("total_steps")
        Integer totalSteps,

        @JsonProperty("average_steps")
        Integer averageSteps,

        @JsonProperty("daily_steps")
        List<DailyStepResponseDto> dailySteps
) {
}
