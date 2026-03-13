package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;

public final class SpecialistJpaMapper {
    private SpecialistJpaMapper() {}

    public Specialist toDomain(SpecialistJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("SpecialistJpa source is null");
        }

        return Specialist.create(
                source.getUserId(),
                source.getDescription(),
                source.getGrade()
        );
    }

    public SpecialistJpa toJpa(Specialist source) {
        if (source == null) {
            throw new IllegalArgumentException("Specialist source is null");
        }

        return new SpecialistJpa(
                source.getUserId(),
                source.getDescription(),
                source.getGrade()
        );
    }
}
