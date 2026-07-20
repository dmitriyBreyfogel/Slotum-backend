package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;

public final class OrganizationMemberJpaMapper {
    private OrganizationMemberJpaMapper() {}

    public static OrganizationMember toDomain(OrganizationMemberJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("OrganizationMemberJpa source is null");
        }

        return OrganizationMember.create(
                source.getId().getOrganizationId(),
                source.getId().getSpecialistId()
        );
    }

    public static OrganizationMemberJpa toJpa(OrganizationMember source) {
        if (source == null) {
            throw new IllegalArgumentException("OrganizationMember source is null");
        }

        return new OrganizationMemberJpa(
                source.getOrganizationId(),
                source.getSpecialistUserId()
        );
    }
}
