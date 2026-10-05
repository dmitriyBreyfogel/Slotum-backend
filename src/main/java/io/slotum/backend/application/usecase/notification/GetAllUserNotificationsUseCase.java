package io.slotum.backend.application.usecase.notification;

import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetAllUserNotificationsUseCase {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public GetAllUserNotificationsUseCase(
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> execute(Long userId) {
        validateUserId(userId);

        return notificationRepository.findByUserId(userId);
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
