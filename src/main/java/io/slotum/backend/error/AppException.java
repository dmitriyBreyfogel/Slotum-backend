package io.slotum.backend.error;

import java.util.Map;

public final class AppException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, Object> details;

    private AppException(ErrorCode code, String logMessage, Map<String, Object> details) {
        super(logMessage);
        this.code = code;
        this.details = (details == null) ? Map.of() : Map.copyOf(details);
    }

    public static AppException build(ErrorCode code, String logMessage, Map<String, Object> details) {
        return new AppException(code, logMessage, details);
    }

    public static AppException build(ErrorCode code, String logMessage) {
        return new AppException(code, logMessage, null);
    }

    public static AppException build(ErrorCode code) {
        return new AppException(code, null, null);
    }

    public ErrorCode getCode() {
        return code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}