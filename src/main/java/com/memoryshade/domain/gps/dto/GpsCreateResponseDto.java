package com.memoryshade.domain.gps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.memoryshade.domain.gps.model.Gps;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GpsCreateResponseDto(

        @JsonProperty("zone_id")
        Long zoneId,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("guardian_id")
        Long guardianId,
        String title,
        Double latitude,
        Double longitude,

        @JsonProperty("radius_meter")
        Integer radiusMeter
) {
    public static GpsCreateResponseDto fromGps(Gps zone) {
        return new GpsCreateResponseDto(
                zone.getZoneId(),
                zone.getUser().getUserId(),
                zone.getGuardian().getUserId(),
                zone.getTitle(),
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getRadiusMeter()
        );
    }
}
