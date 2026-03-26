package io.slotum.backend.domain.appointment;

public enum AppointmentStatus {
    FREE("FREE"),
    BOOKED("BOOKED"),
    CANCELLED("CANCELLED");

    private final String value;

    AppointmentStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
