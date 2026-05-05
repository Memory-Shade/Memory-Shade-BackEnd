package com.memoryshade.domain.report.controller;

import com.memoryshade.domain.report.dto.StatusSummaryResponseDto;
import com.memoryshade.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

  private final ReportService reportService;

  @GetMapping("/users/{userId}/status-summary")
  public ResponseEntity<StatusSummaryResponseDto> getStatusSummary(
      @AuthenticationPrincipal Long loginUserId,
      @PathVariable Long userId
  ) {
    return ResponseEntity.ok(
        reportService.getStatusSummary(loginUserId, userId)
    );
  }
}