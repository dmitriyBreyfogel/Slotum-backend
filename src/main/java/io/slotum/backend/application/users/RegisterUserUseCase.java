package io.slotum.backend.application.users;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Result execute(Command command) {
        User userToSave = User.create(
                null,
                command.surname(),
                command.firstName(),
                command.secondName(),
                command.email(),
                command.password(),
                command.phone()
        );

        String normalizedEmail = userToSave.getEmail().value();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw AppException.build(
                    ErrorCode.USER_EMAIL_ALREADY_EXISTS,
                    "User email already exists",
                    Map.of("email", normalizedEmail)
            );
        }

        User savedUser = userRepository.save(userToSave);

        return new Result(savedUser.getId(), savedUser.getEmail().value());
    }

    public record Command(
            String surname,
            String firstName,
            String secondName,
            String email,
            String password,
            String phone
    ) {
    }

    public record Result(
            Long userId,
            String email
    ) {
    }
}