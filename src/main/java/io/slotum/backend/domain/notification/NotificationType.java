package io.slotum.backend.domain.notification;

/**
 * Типы системных уведомлений
 */
public enum NotificationType {

    /**
     * Создана заявка на запись
     */
    BOOKING_REQUEST_CREATED,

    /**
     * Заявка на запись принята
     */
    BOOKING_ACCEPTED,

    /**
     * Заявка на запись отклонена
     */
    BOOKING_REJECTED,

    /**
     * Заявка на запись отменена
     */
    BOOKING_CANCELLED
}
