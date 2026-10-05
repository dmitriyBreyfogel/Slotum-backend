package io.slotum.backend.application.usecase.user;

import io.slotum.backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllUserUseCase {
    private final UserRepository userRepository;

    public DeleteAllUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute() {
        userRepository.deleteAll();
    }
}
