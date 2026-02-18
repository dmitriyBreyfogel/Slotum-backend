package io.slotum.backend.error;

import java.util.Map;

public abstract class DomainException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, Object> details;

    protected DomainException(ErrorCode code, String logMessage, Map<String, Object> details) {
        super(logMessage);
        this.code = code;
        this.details = (details == null) ? Map.of() : Map.copyOf(details);
    }

    public ErrorCode getErrorCode() {
        return code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}