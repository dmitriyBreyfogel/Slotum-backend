package io.slotum.backend.api.slotBookingRequest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание заявки на запись в слот.
 *
 * @param slotId идентификатор слота
 * @param message сообщение специалисту, может быть {@code null}
 */
public record CreateSlotBookingRequest(
        @NotNull(message = "SlotId is null")
        @Positive(message = "SlotId is not positive")
        Long slotId,

        @Size(max = 1024, message = "Too long message")
        String message
) {
}
