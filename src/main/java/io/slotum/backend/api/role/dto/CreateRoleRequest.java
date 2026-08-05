package io.slotum.backend.api.role.dto;

import io.slotum.backend.domain.role.RoleNames;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание роли.
 *
 * @param name название роли
 * @param description описание роли, может быть {@code null}
 */
public record CreateRoleRequest(
        @NotNull(message = "Name is blank")
        @Size(max = 50, message = "Too long name")
        RoleNames name,

        @Size(max = 255, message = "Too long description")
        String description
) {
}
