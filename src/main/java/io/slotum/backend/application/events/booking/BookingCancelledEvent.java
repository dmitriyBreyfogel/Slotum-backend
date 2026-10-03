package io.slotum.backend.application.events.booking;

import java.time.LocalDateTime;

/**
 * Событие успешной отмены отправленной заявки на запись
 *
 * @param requestId идентификатор заявки на запись
 * @param specialistId идентификатор специалиста, заявку на запись к которому отменили
 * @param slotStartsAt время начала слота, который отменили
 */
public record BookingCancelledEvent(
        Long requestId,
        Long specialistId,
        LocalDateTime slotStartsAt
) {}