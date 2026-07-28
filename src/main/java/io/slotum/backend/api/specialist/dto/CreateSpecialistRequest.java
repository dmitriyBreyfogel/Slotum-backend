package io.slotum.backend.api.specialist.dto;

/**
 * Данные запроса на создание специалиста.
 *
 * @param userId идентификатор пользователя
 * @param description описание специалиста, может быть {@code null}
 * @param grade оценка специалиста, может быть {@code null}
 */
public record CreateSpecialistRequest(
        Long userId,
        String description,
        Double grade
) {
}
