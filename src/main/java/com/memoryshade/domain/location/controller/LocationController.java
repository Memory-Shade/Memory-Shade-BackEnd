package com.memoryshade.domain.location.controller;

import com.memoryshade.domain.location.dto.DailyWanderingCountResponseDto;
import com.memoryshade.domain.location.dto.LocationUpdateRequestDto;
import com.memoryshade.domain.location.dto.LocationUpdateResponseDto;
import com.memoryshade.domain.location.dto.WeeklyWanderingAverageResponseDto;
import com.memoryshade.domain.location.service.LocationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/me/wandering/detect")
    public ResponseEntity<LocationUpdateResponseDto> detectWandering(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody LocationUpdateRequestDto request
    ) {
        return ResponseEntity.ok(locationService.updateMyLocation(loginUserId, request));
    }

    @GetMapping("/{userId}/wandering/weekly-average")
    public ResponseEntity<WeeklyWanderingAverageResponseDto> getWeeklyWanderingAverage(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @RequestParam(name = "base_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate baseDate
    ) {
        return ResponseEntity.ok(locationService.getWeeklyWanderingAverage(loginUserId, userId, baseDate));
    }

    @GetMapping("/{userId}/wandering/daily-count")
    public ResponseEntity<DailyWanderingCountResponseDto> getDailyWanderingCount(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return ResponseEntity.ok(locationService.getDailyWanderingCount(loginUserId, userId, date));
    }
}
