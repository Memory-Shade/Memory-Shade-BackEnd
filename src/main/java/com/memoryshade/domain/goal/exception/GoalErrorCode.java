package com.memoryshade.domain.goal.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GoalErrorCode implements ErrorCode {

    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "목표를 찾을 수 없습니다."),
    TODAY_DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘 작성된 일기가 없습니다"),
    GOAL_RECORD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "오늘 목표 달성 여부는 이미 기록되었습니다");


    private final HttpStatus httpStatus;
    private final String message;
}