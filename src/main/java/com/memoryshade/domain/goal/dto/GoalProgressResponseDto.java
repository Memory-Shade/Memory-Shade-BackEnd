package com.memoryshade.domain.goal.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record GoalProgressResponseDto(

        @JsonProperty("start_date")
        LocalDate startDate,

        @JsonProperty("end_date")
        LocalDate endDate,
        double progress
)
{}
