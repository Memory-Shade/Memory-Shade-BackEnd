package com.memoryshade.domain.recall.controller;

import com.memoryshade.domain.recall.dto.RecallQuizMessagesReadResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizResultResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizSessionCreateResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizTextRequestDto;
import com.memoryshade.domain.recall.dto.RecallQuizTextResponseDto;
import com.memoryshade.domain.recall.dto.RecallQuizWeeklyAverageComparisonResponseDto;
import com.memoryshade.domain.recall.service.RecallQuizService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recall-quiz-sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RecallQuizController {

  private final RecallQuizService recallQuizService;

  @PostMapping
  public ResponseEntity<RecallQuizSessionCreateResponseDto> createRecallQuizSession(
      @AuthenticationPrincipal Long loginUserId
  ) {
    return ResponseEntity.ok(recallQuizService.createRecallQuizSession(loginUserId));
  }

  @PostMapping("/{recallQuizSessionId}/messages/text")
  public ResponseEntity<RecallQuizTextResponseDto> submitRecallQuizText(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long recallQuizSessionId,
      @RequestBody RecallQuizTextRequestDto request
  ) {
    return ResponseEntity.ok(
        recallQuizService.submitRecallQuizText(loginUserId, recallQuizSessionId, request)
    );
  }

  @PostMapping(
      value = "/{recallQuizSessionId}/messages/voice",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<RecallQuizTextResponseDto> submitRecallQuizVoice(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long recallQuizSessionId,
      @RequestPart("file") MultipartFile file
  ) {
    return ResponseEntity.ok(
        recallQuizService.submitRecallQuizVoice(loginUserId, recallQuizSessionId, file)
    );
  }

  @GetMapping("/{recallQuizSessionId}/messages")
  public ResponseEntity<RecallQuizMessagesReadResponseDto> getRecallQuizMessages(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long recallQuizSessionId
  ) {
    return ResponseEntity.ok(
        recallQuizService.getRecallQuizMessages(loginUserId, recallQuizSessionId)
    );
  }

  @GetMapping("/{recallQuizSessionId}/result")
  public ResponseEntity<RecallQuizResultResponseDto> getRecallQuizResult(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long recallQuizSessionId
  ) {
    return ResponseEntity.ok(
        recallQuizService.getRecallQuizResult(loginUserId, recallQuizSessionId)
    );
  }

  @GetMapping(
      value = "/{recallQuizSessionId}/questions/{recallQuizQuestionId}/tts",
      produces = "audio/mpeg"
  )
  public ResponseEntity<byte[]> getRecallQuizQuestionTts(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long recallQuizSessionId,
      @PathVariable Long recallQuizQuestionId
  ) {
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("audio/mpeg"))
        .body(recallQuizService.getRecallQuizQuestionTts(
            loginUserId,
            recallQuizSessionId,
            recallQuizQuestionId
        ));
  }

  @GetMapping("/weekly-averages/comparison")
  public ResponseEntity<RecallQuizWeeklyAverageComparisonResponseDto> getWeeklyRecallQuizAverageComparison(
      @AuthenticationPrincipal Long loginUserId,
      @RequestParam("user_id") Long userId
  ) {
    return ResponseEntity.ok(
        recallQuizService.getWeeklyRecallQuizAverageComparison(loginUserId, userId)
    );
  }
}