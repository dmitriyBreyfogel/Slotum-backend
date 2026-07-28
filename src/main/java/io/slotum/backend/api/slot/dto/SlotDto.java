package io.slotum.backend.api.slot.dto;

import io.slotum.backend.domain.slot.SlotStatus;

import java.time.LocalDateTime;

/**
 * Данные слота.
 *
 * @param id идентификатор слота
 * @param startsAt время начала слота
 * @param endsAt время окончания слота
 * @param status статус слота
 * @param specialistUserId идентификатор пользователя специалиста
 * @param customerId идентификатор записанного пользователя, может быть {@code null}
 * @param organizationId идентификатор организации
 */
public record SlotDto(
        Long id,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        SlotStatus status,
        Long specialistUserId,
        Long customerId,
        Long organizationId
) {
}
