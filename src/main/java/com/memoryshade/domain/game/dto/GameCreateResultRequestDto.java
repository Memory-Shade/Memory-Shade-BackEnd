package com.memoryshade.domain.game.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GameCreateResultRequestDto(
        @NotNull
        @Min(value = 1, message = "초 수는 1 이상이어야 합니다.")
        Integer score
) {}
