package com.memoryshade.domain.location.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationUpdateResponseDto(
        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("is_outside_safe_zone")
        boolean outsideSafeZone,

        @JsonProperty("previous_outside_safe_zone")
        boolean previousOutsideSafeZone,

        @JsonProperty("wandering_detected")
        boolean wanderingDetected,

        @JsonProperty("safe_zone_id")
        Long safeZoneId,

        @JsonProperty("safe_zone_radius_meter")
        Integer safeZoneRadiusMeter,

        @JsonProperty("distance_meter")
        Double distanceMeter,

        @JsonProperty("wandering_count")
        long wanderingCount
) {
}
