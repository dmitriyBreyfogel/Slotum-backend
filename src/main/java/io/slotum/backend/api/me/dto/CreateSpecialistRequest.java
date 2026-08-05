package io.slotum.backend.api.me.dto;

import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание специалиста для текущего пользователя.
 *
 * @param description описание специалиста, может быть {@code null}
 * @param grade оценка специалиста, может быть {@code null}
 */
public record CreateSpecialistRequest(
        @Size(max = 1024, message = "Too long description")
        String description,

        Double grade
) {
}
