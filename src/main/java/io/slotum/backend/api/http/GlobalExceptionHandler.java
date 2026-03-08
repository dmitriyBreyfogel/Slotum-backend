package io.slotum.backend.api.http;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        HttpStatus status = mapStatus(ex.getCode());
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        ex.getCode().name(),
                        status.value(),
                        ex.getMessage(),
                        ex.getDetails(),
                        Instant.now().toString()
                )
        );
    }

    private HttpStatus mapStatus(ErrorCode code) {
        return switch (code) {
            case USER_EMAIL_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case INVALID_USER_ID,
                 INVALID_USER_SURNAME,
                 INVALID_USER_FIRSTNAME,
                 INVALID_USER_EMAIL,
                 INVALID_USER_PASSWORD,
                 INVALID_USER_PHONE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
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
