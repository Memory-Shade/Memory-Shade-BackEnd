package com.memoryshade.domain.game.exception;

import com.memoryshade.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {

    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 게임을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
