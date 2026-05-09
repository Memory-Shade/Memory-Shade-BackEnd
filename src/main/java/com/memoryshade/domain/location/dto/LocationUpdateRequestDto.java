package com.memoryshade.domain.location.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LocationUpdateRequestDto(

        @NotNull(message = "위도를 입력해주세요")
        @DecimalMin(value = "-90.0", message = "위도 범위가 올바르지 않습니다")
        @DecimalMax(value = "90.0", message = "위도 범위가 올바르지 않습니다")
        Double latitude,

        @NotNull(message = "경도를 입력해주세요")
        @DecimalMin(value = "-180.0", message = "경도 범위가 올바르지 않습니다")
        @DecimalMax(value = "180.0", message = "경도 범위가 올바르지 않습니다")
        Double longitude
) {
}
