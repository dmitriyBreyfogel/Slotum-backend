package io.slotum.backend.api.permission.dto;

/**
 * Данные запроса на создание разрешения.
 *
 * @param code код разрешения
 * @param description описание разрешения, может быть {@code null}
 */
public record CreatePermissionRequest(
        String code,
        String description
) {
}
