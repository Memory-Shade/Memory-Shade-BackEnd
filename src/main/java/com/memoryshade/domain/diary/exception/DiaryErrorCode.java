package com.memoryshade.domain.diary.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {

    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 하루 기록을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
