package com.memoryshade.domain.notification.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
