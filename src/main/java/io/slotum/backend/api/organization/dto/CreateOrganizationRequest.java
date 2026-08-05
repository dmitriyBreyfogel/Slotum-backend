package io.slotum.backend.api.organization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание организации.
 *
 * @param name название организации
 * @param description описание организации, может быть {@code null}
 */
public record CreateOrganizationRequest(
        @NotBlank(message = "Name is blank")
        @Size(max = 255, message = "Too long name")
        String name,

        @Size(max = 255, message = "Too long description")
        String description
) {
}
