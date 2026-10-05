package io.slotum.backend.application.usecase.notification;

import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class DeleteByIdNotificationUseCase {
    private final NotificationRepository notificationRepository;

    public DeleteByIdNotificationUseCase(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    public Notification execute(Long id) {
        Optional<Notification> deleted = notificationRepository.deleteById(id);

        if (deleted.isEmpty()) {
            throw AppException.build(
                    ErrorCode.NOTIFICATION_NOT_FOUND,
                    "Notification not found",
                    Map.of("id", id)
            );
        }

        return deleted.get();
    }
}
