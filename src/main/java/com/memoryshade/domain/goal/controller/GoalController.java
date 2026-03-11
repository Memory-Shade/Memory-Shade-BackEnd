package com.memoryshade.domain.goal.controller;

import com.memoryshade.domain.goal.dto.GoalCreateRequestDto;
import com.memoryshade.domain.goal.dto.GoalCreateResponseDto;
import com.memoryshade.domain.goal.dto.GoalGetResponseDto;
import com.memoryshade.domain.goal.dto.GoalProgressResponseDto;
import com.memoryshade.domain.goal.service.GoalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalCreateResponseDto> createGoal(
            @AuthenticationPrincipal Long loginUserId,
            @Valid @RequestBody GoalCreateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.create(loginUserId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<GoalGetResponseDto> getMeGoal(@AuthenticationPrincipal Long loginUserId) {
    return ResponseEntity.ok(goalService.getMeGoal(loginUserId));
    }

    @GetMapping("/progress")
    public ResponseEntity<GoalProgressResponseDto> getGoalProgress(@AuthenticationPrincipal Long loginUserId) {
        return ResponseEntity.ok(goalService.getProgress(loginUserId));
    }
}
