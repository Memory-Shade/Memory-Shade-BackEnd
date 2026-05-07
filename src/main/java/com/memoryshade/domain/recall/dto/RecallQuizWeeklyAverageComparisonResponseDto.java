package com.memoryshade.domain.recall.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RecallQuizWeeklyAverageComparisonResponseDto(
    @JsonProperty("this_week_score")
    int thisWeekScore,

    @JsonProperty("average_score")
    int averageScore,

    @JsonProperty("change_rate")
    int changeRate,

    @JsonProperty("has_data")
    boolean hasData,

    String description
) {
}