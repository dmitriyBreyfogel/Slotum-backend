package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;

public final class AppointmentJpaMapper {
    private AppointmentJpaMapper() {
    }

    public static Appointment toDomain(AppointmentJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("AppointmentJpa source is null");
        }

        return Appointment.restore(
                source.getId(),
                source.getStartsAt(),
                source.getEndsAt(),
                source.getStatus(),
                source.getSpecialist().getUserId(),
                source.getCustomer() == null ? null : source.getCustomer().getId(),
                source.getOrganization().getId()
        );
    }

    public static AppointmentJpa toJpa(
            Appointment source,
            SpecialistJpa specialist,
            UserJpa customer,
            OrganizationJpa organization
    ) {
        if (source == null) {
            throw new IllegalArgumentException("Appointment source is null");
        }

        return new AppointmentJpa(
                source.getId(),
                source.getStartsAt(),
                source.getEndsAt(),
                source.getStatus(),
                specialist,
                source.getCustomerId() == null ? null : customer,
                organization
        );
    }
}
