package com.memoryshade.domain.gps.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GpsErrorCode implements ErrorCode {

    UNAUTHORIZED_GUARDIAN(HttpStatus.UNAUTHORIZED, "인증된 보호자 정보가 없습니다"),
    GUARDIAN_ONLY(HttpStatus.FORBIDDEN, "보호자만 안전 지역을 설정할 수 있습니다"),
    TARGET_USER_ONLY(HttpStatus.BAD_REQUEST, "안전 지역 대상은 USER 역할이어야 합니다"),
    SELF_SET_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인 계정에는 안전 지역을 설정할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
