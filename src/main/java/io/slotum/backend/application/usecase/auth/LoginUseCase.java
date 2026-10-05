package io.slotum.backend.application.usecase.auth;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.security.jwt.JwtService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoginUseCase {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginUseCase(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Result execute(Command command) {
        if (command.email() == null || command.email().isBlank()) {
            throw AppException.build(
                    ErrorCode.AUTH_INVALID_CREDENTIALS,
                    "Invalid credentials"
            );
        }

        User user = userRepository.findByEmail(command.email().trim().toLowerCase())
                .orElseThrow(() -> AppException.build(
                        ErrorCode.AUTH_INVALID_CREDENTIALS,
                        "Invalid credentials",
                        Map.of("email", command.email())
                ));

        if (!user.matchesPassword(command.password())) {
            throw AppException.build(
                    ErrorCode.AUTH_INVALID_CREDENTIALS,
                    "Invalid credentials",
                    Map.of("email", command.email())
            );
        }

        Set<String> roles = userRepository.findRolesById(user.getId())
                .stream()
                .map(RoleNames::name)
                .collect(Collectors.toSet());

        Set<String> permissions = userRepository.findPermissionsById(user.getId())
                .stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        String accessToken = jwtService.issueAccessToken(user.getId(), user.getEmail().value(), roles, permissions);
        return new Result(accessToken, "Bearer");
    }

    public record Command(String email, String password) {
    }

    public record Result(String accessToken, String tokenType) {
    }
}

