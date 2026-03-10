package com.memoryshade.domain.schedule.controller;

import com.memoryshade.domain.schedule.dto.ScheduleRequestDto;
import com.memoryshade.domain.schedule.dto.ScheduleResponseDto;
import com.memoryshade.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody ScheduleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(loginUserId, request));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.update(loginUserId, scheduleId, request));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long scheduleId) {
        scheduleService.delete(userId, scheduleId);
        return ResponseEntity.noContent().build();
    }
}
