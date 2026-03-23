package com.memoryshade.domain.chat.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

  UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다"),
  CHAT_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 세션을 찾을 수 없습니다"),
  CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅 메시지를 찾을 수 없습니다"),
  EMPTY_AUDIO_FILE(HttpStatus.BAD_REQUEST, "음성 파일이 비어 있습니다"),
  STT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "음성 변환에 실패했습니다"),
  AI_RESPONSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 생성에 실패했습니다");

  private final HttpStatus httpStatus;
  private final String message;
}