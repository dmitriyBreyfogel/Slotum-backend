package io.slotum.backend.domain.appointmentRequest;

public enum AppointmentRequestStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED");

    private final String value;

    AppointmentRequestStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
