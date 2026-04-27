package com.memoryshade.domain.recall.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecallErrorCode implements ErrorCode {

  UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다"),
  RECALL_QUIZ_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "회상 퀴즈 세션을 찾을 수 없습니다"),
  RECALL_QUIZ_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 회상 퀴즈입니다"),
  RECALL_QUIZ_TEXT_EMPTY(HttpStatus.BAD_REQUEST, "회상 퀴즈 답변이 비어 있습니다"),
  RECALL_QUIZ_AUDIO_EMPTY(HttpStatus.BAD_REQUEST, "회상 퀴즈 음성 파일이 비어 있습니다"),
  RECENT_DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "최근 회상 질문에 사용할 기록이 없습니다"),
  RECALL_QUIZ_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "현재 진행 중인 회상 질문이 없습니다"),
  RECALL_QUIZ_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회상 퀴즈 생성에 실패했습니다"),
  RECALL_QUIZ_EVALUATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회상 퀴즈 답변 평가에 실패했습니다"),
  RECALL_QUIZ_STT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회상 퀴즈 음성 변환에 실패했습니다");

  private final HttpStatus httpStatus;
  private final String message;
}