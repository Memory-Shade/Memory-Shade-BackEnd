package com.memoryshade.domain.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record DailyWanderingCountResponseDto(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("record_date")
        LocalDate recordDate,

        @JsonProperty("wandering_count")
        Long wanderingCount
) {
}
