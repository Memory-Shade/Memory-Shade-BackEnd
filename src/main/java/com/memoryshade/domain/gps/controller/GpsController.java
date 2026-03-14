package com.memoryshade.domain.gps.controller;

import com.memoryshade.domain.gps.dto.GpsRequestDto;
import com.memoryshade.domain.gps.dto.GpsResponseDto;
import com.memoryshade.domain.gps.service.GpsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class GpsController {

    private final GpsService gpsService;

    @PostMapping("/{userId}/safe-zones")
    public ResponseEntity<GpsResponseDto> create(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @Valid @RequestBody GpsRequestDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gpsService.Create(loginUserId, userId, request));
    }

    @GetMapping("/{userId}/safe-zones")
    public ResponseEntity<List<GpsResponseDto>> getSafeZones(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(gpsService.getUserGps(loginUserId, userId));
    }

    @PutMapping("/{userId}/safe-zones/{zoneId}")
    public ResponseEntity<GpsResponseDto> updateZps(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @PathVariable Long zoneId,
            @Valid @RequestBody GpsRequestDto request) {
        return ResponseEntity.ok(gpsService.update(loginUserId, userId, zoneId, request));
    }

    @DeleteMapping("/{userId}/safe-zones/{zoneId}")
    public ResponseEntity<Void> deleteGps(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @PathVariable Long zoneId) {
        gpsService.delete(loginUserId, userId, zoneId);
        return ResponseEntity.noContent().build();
    }
}
