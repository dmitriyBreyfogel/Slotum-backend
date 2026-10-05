package io.slotum.backend.application.usecase.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.domain.user.vo.Email;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetByEmailUserUseCase {
    private final UserRepository userRepository;

    public GetByEmailUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String email) {
        Email emailObj = Email.of(email);
        Optional<User> user = userRepository.findByEmail(emailObj.value());

        if (user.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found",
                    Map.of("email", emailObj.value())
            );
        }

        return user.get();
    }
}
