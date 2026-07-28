package io.slotum.backend.api.role.dto;

import io.slotum.backend.domain.role.RoleNames;

/**
 * Данные запроса на создание роли.
 *
 * @param name название роли
 * @param description описание роли, может быть {@code null}
 */
public record CreateRoleRequest(
        RoleNames name,
        String description
) {
}
