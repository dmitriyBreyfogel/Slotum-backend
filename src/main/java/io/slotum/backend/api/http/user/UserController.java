package io.slotum.backend.api.http.user;

import io.slotum.backend.application.user.CreateUserUseCase;
import io.slotum.backend.application.user.DeleteByIdUserUseCase;
import io.slotum.backend.application.user.GetAllUsersUseCase;
import io.slotum.backend.application.user.GetUserUseCase;
import io.slotum.backend.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteByIdUserUseCase deleteByIdUserUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase,
            DeleteByIdUserUseCase deleteByIdUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteByIdUserUseCase = deleteByIdUserUseCase;
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
        User result =  getUserUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new UserDto(
                        result.getId(),
                        result.getSurname(),
                        result.getFirstName(),
                        result.getSecondName(),
                        result.getEmail().value(),
                        result.getPhone().value()
                )
        );
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> result = getAllUsersUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(user -> new UserDto(
                        user.getId(),
                        user.getSurname(),
                        user.getFirstName(),
                        user.getSecondName(),
                        user.getEmail().value(),
                        user.getPhone().value()
                )).toList()
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable("id") Long id) {
        User result = deleteByIdUserUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new UserDto(
                        result.getId(),
                        result.getSurname(),
                        result.getFirstName(),
                        result.getSecondName(),
                        result.getEmail().value(),
                        result.getPhone().value()
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
