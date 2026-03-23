package com.memoryshade.domain.emotion.controller;

import com.memoryshade.domain.diary.repository.DiaryRepository;
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
  private final DiaryRepository diaryRepository;
  @PostMapping("/{diaryId}/analyze")
  public ResponseEntity<String> analyze(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long diaryId
  ) {
    emotionService.createEmotionAnalysis(loginUserId, diaryId);

    return ResponseEntity.ok(diaryId + "번 일기에 대한 감정 분석이 시작되었습니다.");
  }
}