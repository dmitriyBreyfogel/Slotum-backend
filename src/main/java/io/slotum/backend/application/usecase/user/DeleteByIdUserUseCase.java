package io.slotum.backend.application.usecase.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeleteByIdUserUseCase {
    private final UserRepository userRepository;

    public DeleteByIdUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Long id) {
        return userRepository.deleteById(id).orElseThrow(() -> AppException.build(
                ErrorCode.USER_NOT_FOUND,
                "User not found",
                Map.of("id", id)
        ));
    }
}
