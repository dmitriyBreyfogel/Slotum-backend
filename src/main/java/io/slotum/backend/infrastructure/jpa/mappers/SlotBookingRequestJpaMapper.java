package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;

public final class SlotBookingRequestJpaMapper {
    private SlotBookingRequestJpaMapper() {
    }

    public static SlotBookingRequest toDomain(SlotBookingRequestJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("SlotBookingRequestJpa source is null");
        }

        return SlotBookingRequest.restore(
                source.getId(),
                source.getSlot().getId(),
                source.getCustomer().getId(),
                source.getStatus(),
                source.getMessage(),
                source.getCreatedAt(),
                source.getDecidedAt()
        );
    }

    public static SlotBookingRequestJpa toJpa(
            SlotBookingRequest source,
            SlotJpa slot,
            UserJpa customer
    ) {
        if (source == null) {
            throw new IllegalArgumentException("SlotBookingRequest source is null");
        }

        return new SlotBookingRequestJpa(
                source.getId(),
                slot,
                customer,
                source.getStatus(),
                source.getMessage(),
                source.getCreatedAt(),
                source.getDecidedAt()
        );
    }
}
