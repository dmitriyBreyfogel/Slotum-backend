package io.slotum.backend.api.slotBookingRequest.dto;

/**
 * Данные запроса на создание заявки на запись в слот.
 *
 * @param slotId идентификатор слота
 * @param message сообщение специалисту, может быть {@code null}
 */
public record CreateSlotBookingRequest(
        Long slotId,
        String message
) {
}
