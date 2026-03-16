package com.memoryshade.domain.emotion.controller;

import com.memoryshade.domain.diary.exception.DiaryErrorCode;
import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.emotion.dto.EmotionRequest;
import com.memoryshade.domain.emotion.service.EmotionService;
import com.memoryshade.global.exception.ExceptionList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

  private final EmotionService emotionService;
  private final DiaryRepository diaryRepository;
  @PostMapping("/analyze")
  public ResponseEntity<String> analyze(@RequestBody EmotionRequest request) {
    Diary diary = diaryRepository.findById(request.diaryId())
        .orElseThrow(() -> new ExceptionList(DiaryErrorCode.DIARY_NOT_FOUND));

    emotionService.analyzeAndSave(diary, diary.getContentStt());

    return ResponseEntity.ok(request.diaryId() + "번 일기에 대한 감정 분석이 시작되었습니다.");
  }
}