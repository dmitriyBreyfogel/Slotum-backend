package io.slotum.backend.application.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class DeleteByIdUserUseCase {
    private final UserRepository userRepository;

    public DeleteByIdUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found",
                    Map.of("id", id)
            );
        }

        return userRepository.deleteById(id);
    }
}
