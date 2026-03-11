package com.memoryshade.domain.goal.dto;

import com.memoryshade.domain.goal.model.Goal;

public record GoalGetResponseDto(
        String title
) {
    public static GoalGetResponseDto fromGoal(Goal goal) {
        return new GoalGetResponseDto(goal.getTitle());
    }
}
