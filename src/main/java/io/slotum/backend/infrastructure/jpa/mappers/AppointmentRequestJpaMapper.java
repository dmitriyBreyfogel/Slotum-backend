package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;

public final class AppointmentRequestJpaMapper {
    private AppointmentRequestJpaMapper() {
    }

    public static AppointmentRequest toDomain(AppointmentRequestJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("AppointmentRequestJpa source is null");
        }

        return AppointmentRequest.restore(
                source.getId(),
                source.getSlot().getId(),
                source.getCustomer().getId(),
                source.getStatus(),
                source.getMessage(),
                source.getCreatedAt(),
                source.getDecidedAt()
        );
    }

    public static AppointmentRequestJpa toJpa(
            AppointmentRequest source,
            SlotJpa slot,
            UserJpa customer
    ) {
        if (source == null) {
            throw new IllegalArgumentException("AppointmentRequest source is null");
        }

        return new AppointmentRequestJpa(
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
