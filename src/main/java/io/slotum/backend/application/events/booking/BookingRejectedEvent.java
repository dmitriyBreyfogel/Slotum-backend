package io.slotum.backend.application.events.booking;

import java.time.LocalDateTime;

/**
 * Событие успешного отклонения специалистом заявки на запись
 * @param requestId идентификатор заявки на запись
 * @param customerId идентификатор пользователя, заявку которого отклонили
 * @param slotStartsAt начало слота, заявку записи на который отклонили
 */
public record BookingRejectedEvent(
    Long requestId,
    Long customerId,
    LocalDateTime slotStartsAt
) {}