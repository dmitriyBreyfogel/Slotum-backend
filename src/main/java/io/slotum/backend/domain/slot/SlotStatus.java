package io.slotum.backend.domain.slot;

public enum SlotStatus {
    FREE("FREE"),
    BOOKED("BOOKED"),
    CANCELLED("CANCELLED");

    private final String value;

    SlotStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
