package io.slotum.backend.application.events.booking;

import java.time.LocalDateTime;

/**
 * Событие успешного создания заявки на запись.
 * Публикуется после сохранения заявки.
 *
 * @param requestId идентификатор сохранённой заявки на запись
 * @param slotId идентификатор слота
 * @param customerId идентификатор пользователя, подавшего заявку
 * @param specialistUserId идентификатор пользователя-специалиста, которому адресована заявка
 * @param customerFirstName имя пользователя, подавшего заявку
 * @param customerSurname фамилия пользователя, подавшего заявку
 * @param slotStartsAt время начала слота
 */
public record BookingRequestCreatedEvent(
        Long requestId,
        Long slotId,
        Long customerId,
        Long specialistUserId,
        String customerFirstName,
        String customerSurname,
        LocalDateTime slotStartsAt
) {}
