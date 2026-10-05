package io.slotum.backend.api.role.dto;

import io.slotum.backend.domain.role.RoleNames;

/**
 * Данные роли.
 *
 * @param id идентификатор роли
 * @param name название роли
 * @param description описание роли, может быть {@code null}
 */
public record RoleDto(
        Long id,
        RoleNames name,
        String description
) {
}
