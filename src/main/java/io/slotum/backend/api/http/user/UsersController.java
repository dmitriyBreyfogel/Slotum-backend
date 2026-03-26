package io.slotum.backend.api.http.user;

import io.slotum.backend.application.user.CreateUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final CreateUserUseCase createUserUseCase;

    public UsersController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<RegisterUserResponse> create(@RequestBody RegisterUserRequest request) {
        CreateUserUseCase.Result result = createUserUseCase.execute(
                new CreateUserUseCase.Command(
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

    public record RegisterUserResponse(
            Long userId,
            String email
    ) {
    }
}
