package com.memoryshade.domain.step.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DailyStepSaveRequestDto(

        @NotNull(message = "기록 날짜를 입력해주세요")
        LocalDate recordDate,

        @NotNull(message = "걸음 수를 입력해주세요")
        @Min(value = 0, message = "걸음 수는 0 이상이어야 합니다")
        Integer stepCount
) {
}
