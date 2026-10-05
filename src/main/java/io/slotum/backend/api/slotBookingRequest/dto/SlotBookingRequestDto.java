package io.slotum.backend.api.slotBookingRequest.dto;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;

import java.time.LocalDateTime;

/**
 * Данные заявки на запись в слот.
 *
 * @param id идентификатор заявки
 * @param slotId идентификатор слота
 * @param customerId идентификатор пользователя, подавшего заявку
 * @param status статус заявки
 * @param message сообщение специалисту, может быть {@code null}
 * @param createdAt время создания заявки
 * @param decidedAt время принятия решения, может быть {@code null}
 */
public record SlotBookingRequestDto(
        Long id,
        Long slotId,
        Long customerId,
        SlotBookingRequestStatus status,
        String message,
        LocalDateTime createdAt,
        LocalDateTime decidedAt
) {
}
