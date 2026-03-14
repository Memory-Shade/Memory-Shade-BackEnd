package com.memoryshade.domain.gps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.memoryshade.domain.gps.model.Gps;

public record GpsResponseDto(

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
    public static GpsResponseDto fromGps(Gps zone) {
        return new GpsResponseDto(
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
