package com.memoryshade.domain.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GameWeeklyAverageComparisonResponseDto(
    @JsonProperty("today_score")
    Integer todayScore,

    @JsonProperty("average_score")
    int averageScore,

    @JsonProperty("improvement_rate")
    int improvementRate,

    @JsonProperty("has_data")
    boolean hasData,

    String description
) {
}