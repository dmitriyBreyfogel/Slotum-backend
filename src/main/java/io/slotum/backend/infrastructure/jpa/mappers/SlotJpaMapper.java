package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;

public final class SlotJpaMapper {
    private SlotJpaMapper() {
    }

    public static Slot toDomain(SlotJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("SlotJpa source is null");
        }

        return Slot.restore(
                source.getId(),
                source.getStartsAt(),
                source.getEndsAt(),
                source.getStatus(),
                source.getSpecialist().getUserId(),
                source.getCustomer() == null ? null : source.getCustomer().getId(),
                source.getOrganization().getId()
        );
    }

    public static SlotJpa toJpa(
            Slot source,
            SpecialistJpa specialist,
            UserJpa customer,
            OrganizationJpa organization
    ) {
        if (source == null) {
            throw new IllegalArgumentException("Slot source is null");
        }

        return new SlotJpa(
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
