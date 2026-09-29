package io.slotum.backend.application.events.booking;

/**
 * Событие успешного создания заявки на запись.
 * Публикуется после сохранения заявки.
 *
 * @param requestId идентификатор сохранённой заявки на запись
 * @param slotId идентификатор слота
 * @param customerId идентификатор пользователя, подавшего заявку
 * @param specialistUserId идентификатор пользователя-специалиста, которому адресована заявка
 */
public record BookingRequestCreatedEvent(
        Long requestId,
        Long slotId,
        Long customerId,
        Long specialistUserId
) {}
