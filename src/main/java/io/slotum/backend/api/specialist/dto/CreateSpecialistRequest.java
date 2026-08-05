package io.slotum.backend.api.specialist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание специалиста.
 *
 * @param userId идентификатор пользователя
 * @param description описание специалиста, может быть {@code null}
 * @param grade оценка специалиста, может быть {@code null}
 */
public record CreateSpecialistRequest(
        @NotNull(message = "UserId is null")
        @Positive(message = "UserId is not positive")
        Long userId,

        @Size(max = 1024, message = "Too long description")
        String description,

        Double grade
) {
}
