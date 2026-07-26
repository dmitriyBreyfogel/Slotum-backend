package io.slotum.backend.api.controllers.auth;

import io.slotum.backend.application.auth.LoginUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginUseCase.Result result = loginUseCase.execute(
                new LoginUseCase.Command(request.email(), request.password())
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new LoginResponse(result.accessToken(), result.tokenType())
        );
    }

    public record LoginRequest(String email, String password) {
    }

    public record LoginResponse(String accessToken, String tokenType) {
    }
}

