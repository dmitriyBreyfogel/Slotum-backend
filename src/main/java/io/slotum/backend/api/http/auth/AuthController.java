package io.slotum.backend.api.http.auth;

import io.slotum.backend.application.auth.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
        RegisterUserUseCase.Result result = registerUserUseCase.execute(
                new RegisterUserUseCase.Command(
                        request.surname(),
                        request.firstName(),
                        request.secondName(),
                        request.email(),
                        request.password(),
                        request.phone()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterUserResponse(result.userId(), result.email())
        );
    }

    public record RegisterUserRequest(
            String surname,
            String firstName,
            String secondName,
            String email,
            String password,
            String phone
    ) {
    }

    public record RegisterUserResponse(Long userId, String email) {
    }
}
