package com.memoryshade.domain.step.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StepErrorCode implements ErrorCode {

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다"),
    GUARDIAN_ONLY(HttpStatus.FORBIDDEN, "보호자만 사용자 걸음 수를 조회할 수 있습니다"),
    TARGET_USER_ONLY(HttpStatus.BAD_REQUEST, "걸음 수 조회 대상은 USER 역할이어야 합니다");

    private final HttpStatus httpStatus;
    private final String message;
}
