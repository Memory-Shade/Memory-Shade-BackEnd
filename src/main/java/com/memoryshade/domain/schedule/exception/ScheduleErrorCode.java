package com.memoryshade.domain.schedule.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements ErrorCode {

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 일정을 찾을 수 없습니다.");
    //TODO: 일정 만든 사용자만 수정가능하도록 에러 코드 추가

    private final HttpStatus httpStatus;
    private final String message;
}
