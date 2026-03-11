package com.memoryshade.domain.goal.dto;

import jakarta.validation.constraints.NotBlank;

public record GoalCreateRequestDto(

        @NotBlank(message = "목표를 입력해주세요")
        String title
) {
}
