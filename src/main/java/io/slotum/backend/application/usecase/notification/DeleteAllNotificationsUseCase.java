package io.slotum.backend.application.usecase.notification;

import io.slotum.backend.domain.notification.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    public DeleteAllNotificationsUseCase(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    public void execute() {
        notificationRepository.deleteAll();
    }
}
