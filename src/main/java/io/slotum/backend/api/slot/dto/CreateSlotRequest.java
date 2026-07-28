package io.slotum.backend.api.slot.dto;

import io.slotum.backend.domain.slot.SlotStatus;

import java.time.LocalDateTime;

/**
 * Данные запроса на создание слота.
 *
 * @param startsAt время начала слота
 * @param endsAt время окончания слота
 * @param status статус слота
 * @param specialistUserId идентификатор пользователя специалиста
 * @param customerId идентификатор записанного пользователя, может быть {@code null}
 * @param organizationId идентификатор организации
 */
public record CreateSlotRequest(
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        SlotStatus status,
        Long specialistUserId,
        Long customerId,
        Long organizationId
) {
}
