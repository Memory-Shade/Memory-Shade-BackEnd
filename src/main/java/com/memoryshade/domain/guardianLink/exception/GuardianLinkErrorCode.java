package com.memoryshade.domain.guardianLink.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GuardianLinkErrorCode implements ErrorCode {

    UNAUTHORIZED_GUARDIAN(HttpStatus.UNAUTHORIZED, "인증된 보호자 정보가 없습니다"),
    GUARDIAN_ONLY(HttpStatus.FORBIDDEN, "보호자만 사용자 연결이 가능합니다"),
    TARGET_USER_ONLY(HttpStatus.BAD_REQUEST, "연결 대상은 USER 역할이어야 합니다"),
    PHONE_NUMBER_MISMATCH(HttpStatus.BAD_REQUEST, "요청 전화번호와 대상 사용자가 일치하지 않습니다"),
    SELF_LINK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인 계정은 연결할 수 없습니다"),
    ALREADY_LINKED(HttpStatus.CONFLICT, "이미 연결된 사용자입니다");

    private final HttpStatus httpStatus;
    private final String message;
}
