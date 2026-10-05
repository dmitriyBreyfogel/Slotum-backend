package io.slotum.backend.application.usecase.notification;

import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ReadNotificationUseCase {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public ReadNotificationUseCase(
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void execute(Long notificationId, Long userId) {
        Notification notification = findNotification(notificationId);
        validateUserId(userId);

        if (!notification.getUserId().equals(userId)) {
            throw AppException.build(
                    ErrorCode.NOTIFICATION_NOT_FOUND,
                    "Notification not found",
                    Map.of("id", notificationId)
            );
        }

        notificationRepository.read(notificationId, userId);
    }

    private Notification findNotification(Long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);

        if (notification.isEmpty()) {
            throw AppException.build(
                    ErrorCode.NOTIFICATION_NOT_FOUND,
                    "Notification not found",
                    Map.of("id", notificationId)
            );
        }

        return notification.get();
    }

    private void validateUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found",
                    Map.of("id", userId)
            );
        }
    }
}
