package io.slotum.backend.infrastructure.jpa.error;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public interface DatabaseConstraintErrorMapper {
    boolean supports(final String constraintName);

    ErrorCode resolve();

    AppException buildException(Map<String, Object> details);
}