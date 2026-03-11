package com.memoryshade.domain.goal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.goal.model.Goal;

public record GoalCreateResponseDto(

        @JsonProperty("goal_id")
        Long goalId,

        @JsonProperty("user_id")
        Long userId,
        String title
) {
    public static GoalCreateResponseDto fromGoal(Goal goal) {
        return new GoalCreateResponseDto(
                goal.getGoalId(),
                goal.getUser().getUserId(),
                goal.getTitle()
        );
    }
}
