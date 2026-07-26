package io.slotum.backend.api.controllers;

import io.slotum.backend.error.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        HttpStatus status = ex.getCode().httpStatus();
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        ex.getCode().name(),
                        status.value(),
                        defaultMessage(ex),
                        ex.getDetails(),
                        Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        "BAD_REQUEST",
                        status.value(),
                        "Malformed JSON request",
                        Collections.emptyMap(),
                        Instant.now().toString()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        "INTERNAL_ERROR",
                        status.value(),
                        "Unexpected error",
                        Collections.emptyMap(),
                        Instant.now().toString()
                )
        );
    }

    private static String defaultMessage(AppException ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            return ex.getCode().name();
        }
        return message;
    }

    public record ErrorResponse(
            String code,
            int status,
            String message,
            Map<String, Object> details,
            String timestamp
    ) {
    }
}
