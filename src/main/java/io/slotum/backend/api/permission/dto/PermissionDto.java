package io.slotum.backend.api.permission.dto;

/**
 * Данные разрешения.
 *
 * @param id идентификатор разрешения
 * @param code код разрешения
 * @param description описание разрешения, может быть {@code null}
 */
public record PermissionDto(
        Long id,
        String code,
        String description
) {
}
