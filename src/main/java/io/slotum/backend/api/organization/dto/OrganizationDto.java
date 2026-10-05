package io.slotum.backend.api.organization.dto;

/**
 * Данные организации.
 *
 * @param id идентификатор организации
 * @param name название организации
 * @param description описание организации, может быть {@code null}
 * @param grade оценка организации, может быть {@code null}
 */
public record OrganizationDto(
        Long id,
        String name,
        String description,
        Double grade
) {
}
