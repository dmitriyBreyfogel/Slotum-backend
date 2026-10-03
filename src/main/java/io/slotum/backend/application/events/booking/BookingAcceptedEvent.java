package io.slotum.backend.application.events.booking;

import java.time.LocalDateTime;

/**
 * Событие успешного принятия запроса на запись.
 * Публикуется после того, как специалист принимает заявку на запись
 * от пользователя
 *
 * @param requestId идентификатор заявки на запись
 * @param customerId идентификатор пользователя, отправившего заявку
 * @param slotStartsAt время и дата начала слота
 */
public record BookingAcceptedEvent(
    Long requestId,
    Long customerId,
    LocalDateTime slotStartsAt
) {}