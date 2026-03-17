package io.slotum.backend.domain.appointment.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class AppointmentStatus {
    private static final int MAX_LENGTH = 32;
    private final String status;

    public AppointmentStatus(String status) {
        String normalized = normalize(status);
        if (!validate(normalized)) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_STATUS,
                    "Invalid appointment status",
                    Map.of("status", status)
            );
        }
        this.status = normalized;
    }

    public String value() {
        return status;
    }

    public static boolean validate(String status) {
        return status != null && !status.isBlank() && status.length() <= MAX_LENGTH;
    }

    private static String normalize(String status) {
        if (status == null) {
            return null;
        }
        return status.trim();
    }
}