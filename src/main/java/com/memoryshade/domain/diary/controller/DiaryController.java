package com.memoryshade.domain.diary.controller;

import com.memoryshade.domain.diary.dto.DiaryReadResponseDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareRequestDto;
import com.memoryshade.domain.diary.dto.DiaryUpdateShareResponseDto;
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

    @GetMapping("diaries/me")
    public ResponseEntity<List<DiaryReadResponseDto>> getAllDiariesByDate(
            @AuthenticationPrincipal Long loginUserId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(diaryService.getAllDiariesByDate(loginUserId, date));
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

    @PatchMapping("/diaries/{diaryId}/share")
    public ResponseEntity<DiaryUpdateShareResponseDto> updateDiaryShare(
            @AuthenticationPrincipal Long loginUserId,
            @PathVariable Long diaryId,
            @RequestBody DiaryUpdateShareRequestDto request) { //TODO: request 안 받아도 될것 같은데
        return ResponseEntity.ok(diaryService.updateDiaryShare(loginUserId, diaryId));
    }
}
