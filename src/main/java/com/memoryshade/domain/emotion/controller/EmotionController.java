package com.memoryshade.domain.emotion.controller;

import com.memoryshade.domain.diary.model.Diary;
import com.memoryshade.domain.diary.repository.DiaryRepository;
import com.memoryshade.domain.emotion.dto.EmotionRequest;
import com.memoryshade.domain.emotion.service.EmotionService;
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

    Diary existingDiary = diaryRepository.findById(1)
        .orElseThrow(() -> new RuntimeException("1번 일기가 DB에 없습니다."));

    emotionService.analyzeAndSave(existingDiary, request.contentText());

    return ResponseEntity.ok("기존 1번 일기에 대한 감정 분석이 완료되었습니다.");
  }
}