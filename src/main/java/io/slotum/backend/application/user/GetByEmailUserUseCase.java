package io.slotum.backend.application.user;

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
        if (email == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_EMAIL,
                    "User email is null"
            );
        }

        Email emailObj = new Email(email);

        if (!userRepository.existsByEmail(email)) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found",
                    Map.of("email", email)
            );
        }

        return userRepository.findByEmail(email).get();
    }
}
