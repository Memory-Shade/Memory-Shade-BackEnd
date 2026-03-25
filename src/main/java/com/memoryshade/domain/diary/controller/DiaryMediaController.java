package com.memoryshade.domain.diary.controller;

import com.memoryshade.domain.diary.dto.DiaryMediaReadResponseDto;
import com.memoryshade.domain.diary.service.DiaryMediaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DiaryMediaController {

  private final DiaryMediaService diaryMediaService;

  @GetMapping("/{diaryId}/media")
  public ResponseEntity<List<DiaryMediaReadResponseDto>> getDiaryMedias(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long diaryId
  ) {
    return ResponseEntity.ok(
        diaryMediaService.getDiaryMedias(loginUserId, diaryId)
    );
  }
}