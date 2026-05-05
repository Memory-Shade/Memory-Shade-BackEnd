package com.memoryshade.domain.report.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements ErrorCode {

  STATUS_SUMMARY_GENERATION_FAILED(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "상태 종합 요약 생성에 실패했습니다"
  );

  private final HttpStatus httpStatus;
  private final String message;
}