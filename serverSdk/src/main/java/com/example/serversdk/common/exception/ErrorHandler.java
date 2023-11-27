package com.example.serversdk.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedError(final UnauthorizedError e) {
        log.warn("Не авторизирован", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Не авторизирован", e.getMessage(), new Date());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenError(final ForbiddenException e) {
        log.warn("Нет доступа", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Нет доступа", e.getMessage(), new Date());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.warn("Конфликт", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Конфликт", e.getMessage(), new Date());
        return errorResponse;
    }
}
