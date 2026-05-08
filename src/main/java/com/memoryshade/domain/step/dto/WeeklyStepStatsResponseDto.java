package com.memoryshade.domain.step.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WeeklyStepStatsResponseDto(

        @JsonProperty("average_steps")
        Integer averageSteps
) {
}
