package io.slotum.backend.api.user;

import io.slotum.backend.api.user.dto.CreateUserRequest;
import io.slotum.backend.api.user.dto.UserDto;
import io.slotum.backend.application.user.*;
import io.slotum.backend.domain.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class UserController implements UserApi {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final DeleteByIdUserUseCase deleteByIdUserUseCase;
    private final DeleteAllUserUseCase deleteAllUserUseCase;
    private final GetByEmailUserUseCase getByEmailUserUseCase;

    public UserController(
            CreateUserUseCase createUserUseCase,
            GetUserUseCase getUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase,
            DeleteByIdUserUseCase deleteByIdUserUseCase,
            DeleteAllUserUseCase deleteAllUserUseCase,
            GetByEmailUserUseCase getByEmailUserUseCase
    ) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.deleteByIdUserUseCase = deleteByIdUserUseCase;
        this.deleteAllUserUseCase = deleteAllUserUseCase;
        this.getByEmailUserUseCase = getByEmailUserUseCase;
    }

    @Override
    public ResponseEntity<UserDto> create(CreateUserRequest request) {
        User result = createUserUseCase.execute(
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
                        result.getId(),
                        result.getSurname(),
                        result.getFirstName(),
                        result.getSecondName(),
                        result.getEmail().value(),
                        result.getPhone().value()
                )
        );
    }

    @Override
    public ResponseEntity<UserDto> getUser(long id) {
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

    @Override
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

    @Override
    public ResponseEntity<UserDto> getByEmail(String email) {
        User result = getByEmailUserUseCase.execute(email);

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
    
    @Override
    public ResponseEntity<UserDto> deleteUser(Long id) {
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

    @Override
    public ResponseEntity<Void> deleteAllUsers() {
        deleteAllUserUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
