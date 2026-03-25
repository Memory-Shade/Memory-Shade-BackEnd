package com.memoryshade.domain.diary.controller;

import com.memoryshade.domain.diary.dto.DiaryMediaReadResponseDto;
import com.memoryshade.domain.diary.dto.DiaryReadResponseDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareRequestDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareResponseDto;
import com.memoryshade.domain.diary.service.DiaryMediaService;
import com.memoryshade.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DiaryController {

    private final DiaryService diaryService;
    private final DiaryMediaService diaryMediaService;

    @GetMapping("/diaries/me")
    public ResponseEntity<List<DiaryReadResponseDto>> getAllDiariesByDate(
            @AuthenticationPrincipal Long loginUserId,
            @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(diaryService.getAllDiariesByDate(loginUserId, date));
    }

    @GetMapping("/diaries/me/range")
    public ResponseEntity<List<DiaryReadResponseDto>> getAllDiariesByDateRange(
            @AuthenticationPrincipal Long loginUserId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        return ResponseEntity.ok(
                diaryService.getAllDiariesByDateRange(loginUserId, startDate, endDate)
        );
    }

    @GetMapping("/users/{userId}/diaries")
    public ResponseEntity<List<DiaryReadResponseDto>> getUserSharedDiariesByDate(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(
                diaryService.getUserSharedDiariesByDate(loginUserId, userId, date)
        );
    }

    @GetMapping("/users/{userId}/diaries/range")
    public ResponseEntity<List<DiaryReadResponseDto>> getUserSharedDiariesByDateRange(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long userId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ) {
        return ResponseEntity.ok(
                diaryService.getUserSharedDiariesByDateRange(loginUserId, userId, startDate, endDate)
        );
    }

    @PatchMapping("/diaries/{diaryId}/share")
    public ResponseEntity<DiaryUpdateShareResponseDto> updateDiaryShare(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long diaryId,
            @RequestBody DiaryUpdateShareRequestDto request) { //TODO: request 안 받아도 될것 같은데
        return ResponseEntity.ok(diaryService.updateDiaryShare(loginUserId, diaryId));
    }


    @GetMapping("/diaries/{diaryId}/media")
    public ResponseEntity<List<DiaryMediaReadResponseDto>> getDiaryMedias(
        @AuthenticationPrincipal Long loginUserId,
        @PathVariable Long diaryId
    ) {
        return ResponseEntity.ok(
            diaryMediaService.getDiaryMedias(loginUserId, diaryId)
        );
    }

}