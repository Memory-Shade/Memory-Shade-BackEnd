package com.memoryshade.domain.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record WeeklyWanderingAverageResponseDto(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("start_date")
        LocalDate startDate,

        @JsonProperty("end_date")
        LocalDate endDate,

        @JsonProperty("average_wandering_count")
        Double averageWanderingCount
) {
}
