package com.memoryshade.domain.emotion.controller;

import com.memoryshade.domain.emotion.dto.EmotionRecentReadResponseDto;
import com.memoryshade.domain.emotion.service.EmotionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class EmotionController {

  private final EmotionService emotionService;

  @PostMapping("/{diaryId}/analyze")
  public ResponseEntity<String> analyze(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long diaryId
  ) {
    emotionService.createEmotionAnalysis(loginUserId, diaryId);
    return ResponseEntity.ok(diaryId + "번 일기에 대한 감정 분석이 시작되었습니다.");
  }

  @GetMapping
  public ResponseEntity<EmotionRecentReadResponseDto> getRecentEmotionSummary(
      @AuthenticationPrincipal Long loginUserId,
      @RequestParam("user_id") Long userId
  ) {
    return ResponseEntity.ok(
        emotionService.getRecentEmotionSummary(loginUserId, userId)
    );
  }
}