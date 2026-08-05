package io.slotum.backend.api.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание разрешения.
 *
 * @param code код разрешения
 * @param description описание разрешения, может быть {@code null}
 */
public record CreatePermissionRequest(
        @NotBlank(message = "Code is blank")
        @Size(max = 100, message = "Too long code")
        String code,

        @Size(max = 255, message = "Too long description")
        String description
) { }
