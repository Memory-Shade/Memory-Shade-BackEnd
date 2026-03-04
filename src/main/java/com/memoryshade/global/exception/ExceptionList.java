package com.memoryshade.global.exception;

import lombok.Getter;

@Getter
public class ExceptionList extends RuntimeException {
    private final ErrorCode errorCode;

    public ExceptionList(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
