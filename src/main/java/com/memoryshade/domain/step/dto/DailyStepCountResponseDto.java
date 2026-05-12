package com.memoryshade.domain.step.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DailyStepCountResponseDto(

        @JsonProperty("step_count")
        Integer stepCount
) {
}
