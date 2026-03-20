package com.memoryshade.domain.goal.dto;

import com.memoryshade.domain.goal.model.Goal;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.NotBlank;

public record GoalCreateRequestDto(

        @NotBlank(message = "목표를 입력해주세요")
        String title
) {
        public Goal toGoal(User user) {
                return Goal.builder()
                        .user(user)
                        .title(title())
                        .build();
        }
}
