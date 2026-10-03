package io.slotum.backend.application.events.listeners;

import io.slotum.backend.application.events.booking.BookingAcceptedEvent;
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
                "Ваша заявка на запись одобрена",
                message
        );

        notificationRepository.save(notification);
    }
}
