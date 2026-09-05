package io.slotum.backend.domain.notification;

import io.slotum.backend.domain.utils.StringUtils;

import java.time.Instant;

/**
 * Уведомление, адресованное конкретному пользователю.
 *
 * <p>Каждое уведомление имеет тип {@link NotificationType}, определяющий
 * причину отправки, и флаг прочтения {@code isRead}.
 */
public final class Notification {
    private final Long id;
    private final Long userId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final boolean isRead;
    private final Instant createdAt;

    private Notification(
            Long id,
            Long userId,
            NotificationType type,
            String title,
            String message,
            boolean isRead,
            Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    /**
     * Создание объекта уведомления
     * @param userId идентификатор пользователя, которому направлено уведомление
     * @param type тип уведомления
     * @param title заголовок уведомления
     * @param message сообщение уведомления
     * @return созданный объект уведомления по заданным параметрам
     */
    public static Notification create(Long userId, NotificationType type, String title, String message) {
        return restore(null, userId, type, title, message, false, Instant.now());
    }

    /**
     * Создание объекта уведомления без его сообщения, только по заголовку
     * @param userId идентификатор пользователя, которому направлено уведомление
     * @param type тип уведомления
     * @param title заголовок уведомления
     * @return созданный объект уведомления по заданным параметрам
     */
    public static Notification create(Long userId, NotificationType type, String title) {
        return restore(null, userId, type, title, null, false, Instant.now());
    }

    /**
     * Создаёт объект уведомления с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что уведомление новое.
     * @param id идентификатор уведомления
     * @param userId идентификатор пользователя, которому направлено уведомление
     * @param type тип уведомления
     * @param title заголовок уведомления
     * @param message сообщение уведомления (может быть {@code null})
     * @param isRead флаг осведомления с уведомлением. {@code false} если уведомление ещё не прочитано, {@code true} иначе
     * @param createdAt время создания уведомления
     * @return созданный объект уведомления по заданным параметрам
     */
    public static Notification restore(
            Long id,
            Long userId,
            NotificationType type,
            String title,
            String message,
            boolean isRead,
            Instant createdAt
    ) {
        String normalizedTitle = StringUtils.normalize(title);
        String normalizedMessage = StringUtils.normalize(message);

        return new Notification(id, userId, type, normalizedTitle, normalizedMessage, isRead, createdAt);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
