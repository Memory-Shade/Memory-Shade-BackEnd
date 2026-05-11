package com.memoryshade.domain.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoalAchievementRequestDto(
    @NotNull(message = "목표 달성 여부는 필수입니다.")
    @JsonProperty("is_achieved")
    Boolean isAchieved
) {
}