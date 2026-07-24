package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.infrastructure.jpa.entities.PermissionJpa;

public final class PermissionJpaMapper {
    private PermissionJpaMapper() {}

    public static Permission toDomain(PermissionJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("PermissionJpa source is null");
        }

        return Permission.restore(
                source.getId(),
                source.getCode(),
                source.getDescription()
        );
    }

    public static PermissionJpa toJpa(Permission source) {
        if (source == null) {
            throw new IllegalArgumentException("Permission source is null");
        }

        return new PermissionJpa(
                source.getId(),
                source.getCode(),
                source.getDescription()
        );
    }
}
