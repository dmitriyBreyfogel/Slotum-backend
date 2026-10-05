package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.infrastructure.jpa.entities.NotificationJpa;

public final class NotificationJpaMapper {
    private NotificationJpaMapper() {}

    public static Notification toDomain(NotificationJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("NotificationJpa source is null");
        }

        return Notification.restore(
                source.getId(),
                source.getUserId(),
                source.getType(),
                source.getTitle(),
                source.getMessage(),
                source.getIsRead(),
                source.getCreatedAt()
        );
    }

    public static NotificationJpa toJpa(Notification source) {
        if (source == null) {
            throw new IllegalArgumentException("Notification source is null");
        }

        return new NotificationJpa(
                source.getId(),
                source.getUserId(),
                source.getType(),
                source.getTitle(),
                source.getMessage(),
                source.isRead(),
                source.getCreatedAt()
        );
    }
}
