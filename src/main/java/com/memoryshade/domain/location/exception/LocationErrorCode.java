package com.memoryshade.domain.location.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LocationErrorCode implements ErrorCode {

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증된 사용자 정보가 없습니다"),
    USER_ONLY(HttpStatus.FORBIDDEN, "사용자만 위치를 갱신할 수 있습니다"),
    GUARDIAN_ONLY(HttpStatus.FORBIDDEN, "보호자만 배회 횟수를 조회할 수 있습니다"),
    TARGET_USER_ONLY(HttpStatus.BAD_REQUEST, "배회 횟수 조회 대상은 USER 역할이어야 합니다"),
    SAFE_ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 안전 지역이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
