package io.slotum.backend.domain.slotBookingRequest;

public enum SlotBookingRequestStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED");

    private final String value;

    SlotBookingRequestStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
