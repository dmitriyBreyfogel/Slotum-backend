package io.slotum.backend.application.usecase.notification;

import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CountUnreadUserNotificationsUseCase {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public CountUnreadUserNotificationsUseCase(
            UserRepository userRepository,
            NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public long execute(Long userId) {
        validateUserId(userId);

        return notificationRepository.countUnreadByUserId(userId);
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
