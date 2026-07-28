package io.slotum.backend.api.me.dto;

/**
 * Данные запроса на создание специалиста для текущего пользователя.
 *
 * @param description описание специалиста, может быть {@code null}
 * @param grade оценка специалиста, может быть {@code null}
 */
public record CreateSpecialistRequest(
        String description,
        Double grade
) {
}
