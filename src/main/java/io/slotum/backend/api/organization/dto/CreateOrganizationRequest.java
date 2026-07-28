package io.slotum.backend.api.organization.dto;

/**
 * Данные запроса на создание организации.
 *
 * @param name название организации
 * @param description описание организации, может быть {@code null}
 */
public record CreateOrganizationRequest(
        String name,
        String description
) {
}
