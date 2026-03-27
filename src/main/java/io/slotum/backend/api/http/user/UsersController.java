package io.slotum.backend.api.http.user;

import io.slotum.backend.application.user.CreateUserUseCase;
import io.slotum.backend.application.user.GetUserUseCase;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UsersController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest request) {
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
                new UserDto(
                        result.userId(),
                        result.surname(),
                        result.firstName(),
                        result.secondName(),
                        result.email(),
                        result.phone()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") long id) {
        User user =  getUserUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new UserDto(
                        user.getId(),
                        user.getSurname(),
                        user.getFirstName(),
                        user.getSecondName(),
                        user.getEmail().value(),
                        user.getPhone().value()
                )
        );
    }

    public record CreateUserRequest(
            String surname,
            String firstName,
            String secondName,
            String email,
            String password,
            String phone
    ) {
    }

    public record UserDto(
            Long userId,
            String surname,
            String firstName,
            String secondName,
            String email,
            String phone
    ) {
    }
}
