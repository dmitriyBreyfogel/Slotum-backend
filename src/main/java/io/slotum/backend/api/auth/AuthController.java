package io.slotum.backend.api.auth;

import io.slotum.backend.api.auth.dto.LoginRequest;
import io.slotum.backend.api.auth.dto.LoginResponse;
import io.slotum.backend.application.auth.LoginUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class AuthController implements AuthApi {
    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        LoginUseCase.Result result = loginUseCase.execute(
                new LoginUseCase.Command(request.email(), request.password())
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new LoginResponse(result.accessToken(), result.tokenType())
        );
    }
}
