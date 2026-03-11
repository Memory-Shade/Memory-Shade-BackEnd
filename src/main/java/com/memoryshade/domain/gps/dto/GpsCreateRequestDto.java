package com.memoryshade.domain.gps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.gps.model.Gps;
import com.memoryshade.domain.user.model.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GpsCreateRequestDto(

        String title,

        @NotNull(message = "위도를 입력해주세요")
        @Min(value = -90, message = "위도 범위가 올바르지 않습니다")
        @Max(value = 90, message = "위도 범위가 올바르지 않습니다")
        Double latitude,

        @NotNull(message = "경도를 입력해주세요")
        @Min(value = -180, message = "경도 범위가 올바르지 않습니다")
        @Max(value = 180, message = "경도 범위가 올바르지 않습니다")
        Double longitude,

        @JsonProperty("radius_meter")
        @NotNull(message = "반경을 입력해주세요")
        @Min(value = 100, message = "반경은 100m 이상이어야 합니다")
        Integer radiusMeter
) {
        public Gps toGps(User user, User guardian) {
                return Gps.builder()
                        .user(user)
                        .guardian(guardian)
                        .title(title)
                        .latitude(latitude)
                        .longitude(longitude)
                        .radiusMeter(radiusMeter)
                        .build();
        }
}
