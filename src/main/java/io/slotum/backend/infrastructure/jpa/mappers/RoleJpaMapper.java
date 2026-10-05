package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.infrastructure.jpa.entities.RoleJpa;

public final class RoleJpaMapper {
    private RoleJpaMapper() {}

    public static Role toDomain(RoleJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("RoleJpa source is null");
        }

        return Role.restore(
                source.getId(),
                source.getName(),
                source.getDescription()
        );
    }

    public static RoleJpa toJpa(Role source) {
        if (source == null) {
            throw new IllegalArgumentException("Role source is null");
        }

        return new RoleJpa(
                source.getId(),
                source.getName(),
                source.getDescription()
        );
    }
}
