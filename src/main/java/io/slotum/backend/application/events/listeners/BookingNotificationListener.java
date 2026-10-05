package io.slotum.backend.application.events.listeners;

import io.slotum.backend.application.events.booking.BookingAcceptedEvent;
import io.slotum.backend.application.events.booking.BookingCancelledEvent;
import io.slotum.backend.application.events.booking.BookingRejectedEvent;
import io.slotum.backend.application.events.booking.BookingRequestCreatedEvent;
import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.domain.notification.NotificationType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Создаёт уведомления пользователям в ответ на события,
 * связанные с заявками на запись.
 */
@Component
public class BookingNotificationListener {
    private final NotificationRepository notificationRepository;

    private static final DateTimeFormatter SLOT_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "d MMMM 'в' HH:mm",
                    Locale.forLanguageTag("ru")
            );

    public BookingNotificationListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    /**
     * Формирование и сохранение уведомления специалисту о новой заявке на его запись
     * @param event событие успешного создания заявки на запись
     */
    @EventListener
    public void on(BookingRequestCreatedEvent event) {
        String message = "%s %s хочет записаться на %s"
                .formatted(
                        event.customerFirstName(),
                        event.customerSurname(),
                        event.slotStartsAt().format(SLOT_TIME_FORMAT)
                );

        Notification notification = Notification.create(
                event.specialistUserId(),
                NotificationType.BOOKING_REQUEST_CREATED,
                "Новая заявка на запись",
                message
        );

        notificationRepository.save(notification);
    }

    /**
     * Формирование и сохранение уведомления пользователю об одобрении его заявки на запись
     * @param event событие успешного одобрения заявки на запись
     */
    @EventListener
    public void on(BookingAcceptedEvent event) {
        String message = "Ваша заявка на %s принята"
                .formatted(
                        event.slotStartsAt().format(SLOT_TIME_FORMAT)
                );

        Notification notification = Notification.create(
                event.customerId(),
                NotificationType.BOOKING_ACCEPTED,
                "Заявка принята",
                message
        );

        notificationRepository.save(notification);
    }

    /**
     * Формирование и сохранение уведомления пользователю об отклонении его заявки на запись
     * @param event событие успешного отклонения заявки на запись
     */
    @EventListener
    public void on(BookingRejectedEvent event) {
        String message = "Ваша заявка на %s отклонена"
                .formatted(
                        event.slotStartsAt().format(SLOT_TIME_FORMAT)
                );

        Notification notification = Notification.create(
                event.customerId(),
                NotificationType.BOOKING_REJECTED,
                "Заявка отклонена",
                message
        );

        notificationRepository.save(notification);
    }

    /**
     * Формирование и сохранение уведомления специалисту об отмене заявки на запись клиентом
     * @param event событие успешной отмены заявки на запись
     */
    @EventListener
    public void on(BookingCancelledEvent event) {
        String message = "Клиент отменил заявку на запись %s"
                .formatted(
                        event.slotStartsAt().format(SLOT_TIME_FORMAT)
                );

        Notification notification = Notification.create(
                event.specialistUserId(),
                NotificationType.BOOKING_CANCELLED,
                "Заявка отменена",
                message
        );

        notificationRepository.save(notification);
    }
}
