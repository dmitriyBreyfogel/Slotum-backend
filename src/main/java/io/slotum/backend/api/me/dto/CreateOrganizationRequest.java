package io.slotum.backend.api.me.dto;

/**
 * Данные запроса на создание организации текущим специалистом.
 *
 * @param name название организации
 * @param description описание организации, может быть {@code null}
 */
public record CreateOrganizationRequest(
        String name,
        String description
) {
}
