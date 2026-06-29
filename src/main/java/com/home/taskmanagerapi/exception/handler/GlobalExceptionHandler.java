package com.home.taskmanagerapi.exception.handler;

import com.home.taskmanagerapi.exception.custom.TaskNotFoundException;
import com.home.taskmanagerapi.exception.error.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.UUID;

/**
 * Centralised error handler.
 * <p>
 * Policy: the client only ever sees 400, 404, 409 or 500, each with a generic
 * message and a traceId, never field-level validation rules, internal messages or stack
 * traces. The full cause is logged server-side against the traceId.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String INVALID_REQUEST = "Invalid request";
    private static final String INTERNAL_SERVER_ERROR = "Internal server error";
    private static final String RESOURCE_MODIFIED_BY_ANOTHER_REQUEST = "Resource was modified by another request";

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(TaskNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND, ex);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoRoute(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, INVALID_REQUEST, ex);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleConflict(OptimisticLockingFailureException ex) {
        return build(HttpStatus.CONFLICT, RESOURCE_MODIFIED_BY_ANOTHER_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String clientMessage, Exception ex) {
        var traceId = UUID.randomUUID().toString();

        if (status.is5xxServerError()) {
            log.error("[{}] unhandled error", traceId, ex);
        } else {
            log.warn("[{}] {} - {}: {}", traceId, status.value(),
                    ex.getClass().getSimpleName(), ex.getMessage());
        }

        var body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(clientMessage)
                .traceId(traceId)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}