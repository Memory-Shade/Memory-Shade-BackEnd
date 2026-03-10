package com.memoryshade.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ExceptionResponse("VALIDATION_ERROR", msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse("ACCESS_DENIED", "권한이 없습니다"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse("AUTHENTICATION_ERROR", "인증 정보가 올바르지 않습니다"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("INVALID_JSON", "요청 JSON 형식이 잘못되었습니다"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = ex.getName() + " 파라미터에 잘못된 형식의 값이 전달되었습니다";
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("TYPE_MISMATCH", msg));
    }

    @ExceptionHandler(ExceptionList.class)
    public ResponseEntity<ExceptionResponse> handleApplicationException(ExceptionList e) {
        ErrorCode ec = e.getErrorCode();
        ExceptionResponse body = new ExceptionResponse(ec.name(), ec.getMessage());
        return new ResponseEntity<>(body, ec.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("SERVER_ERROR", "서버 에러"));
    }
}
