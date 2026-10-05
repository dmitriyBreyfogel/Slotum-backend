package io.slotum.backend.api.specialist.dto;

/**
 * Данные специалиста.
 *
 * @param userId идентификатор пользователя специалиста
 * @param description описание специалиста, может быть {@code null}
 * @param grade оценка специалиста, может быть {@code null}
 */
public record SpecialistDto(
        Long userId,
        String description,
        Double grade
) {
}
