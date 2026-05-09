package com.memoryshade.domain.step.controller;

import com.memoryshade.domain.step.dto.DailyStepResponseDto;
import com.memoryshade.domain.step.dto.DailyStepSaveRequestDto;
import com.memoryshade.domain.step.dto.WeeklyStepStatsResponseDto;
import com.memoryshade.domain.step.service.StepService;
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
public class StepController {

    private final StepService stepService;

    @PutMapping("/me/steps/daily")
    public ResponseEntity<DailyStepResponseDto> saveDailySteps(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody DailyStepSaveRequestDto request
    ) {
        return ResponseEntity.ok(stepService.saveDailySteps(loginUserId, request));
    }

    @GetMapping("/{userId}/steps/weekly")
    public ResponseEntity<WeeklyStepStatsResponseDto> getUserWeeklyStats(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @RequestParam(name = "base_date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate baseDate
    ) {
        return ResponseEntity.ok(stepService.getUserWeeklyStats(loginUserId, userId, baseDate));
    }
}
