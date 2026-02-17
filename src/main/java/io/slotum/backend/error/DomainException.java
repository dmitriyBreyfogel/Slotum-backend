package io.slotum.backend.error;

import java.util.Map;

public abstract class DomainException extends RuntimeException {
    private final ErrorCode _code;
    private final Map<String, Object> _details;

    protected DomainException(ErrorCode code, String logMessage, Map<String, Object> details) {
        super(logMessage);
        this._code = code;
        this._details = (details == null) ? Map.of() : Map.copyOf(details);
    }

    public ErrorCode getErrorCode() {
        return _code;
    }

    public Map<String, Object> getDetails() {
        return _details;
    }
}