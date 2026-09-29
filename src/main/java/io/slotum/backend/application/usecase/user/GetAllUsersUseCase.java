package io.slotum.backend.application.usecase.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersUseCase {
    private final UserRepository userRepository;

    public GetAllUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> execute() {
        return userRepository.findAll();
    }
}
