package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;

public final class OrganizationJpaMapper {
    private OrganizationJpaMapper() {
    }

    public static Organization toDomain(OrganizationJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("OrganizationJpa source is null");
        }

        return Organization.restore(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getGrade()
        );
    }

    public static OrganizationJpa toJpa(Organization source) {
        if (source == null) {
            throw new IllegalArgumentException("Organization source is null");
        }

        return new OrganizationJpa(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getGrade()
        );
    }
}
