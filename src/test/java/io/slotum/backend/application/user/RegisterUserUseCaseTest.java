package io.slotum.backend.application.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RegisterUserUseCaseTest {

    @Test
    @DisplayName("Не обращается к репозиторию, если surname = null")
    void rejectsNullSurnameBeforeRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                null,
                "John",
                null,
                "test@test.com",
                "Password123!",
                "+79991234567"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_USER_SURNAME, ex.getCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если firstName = blank")
    void rejectsBlankFirstNameBeforeRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Doe",
                "   ",
                null,
                "test@test.com",
                "Password123!",
                "+79991234567"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_USER_FIRSTNAME, ex.getCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если email невалиден")
    void rejectsInvalidEmailBeforeRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Doe",
                "John",
                null,
                "not-an-email",
                "Password123!",
                "+79991234567"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если password невалиден")
    void rejectsInvalidPasswordBeforeRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Doe",
                "John",
                null,
                "test@test.com",
                null,
                "+79991234567"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если phone невалиден")
    void rejectsInvalidPhoneBeforeRepository() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Doe",
                "John",
                null,
                "test@test.com",
                "Password123!",
                "bad phone"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Бросает исключение, если email уже существует (проверка по нормализованному email)")
    void throwsIfEmailAlreadyExists() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Doe",
                "John",
                null,
                "  Test@Test.com  ",
                "Password123!",
                "+79991234567"
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.USER_EMAIL_ALREADY_EXISTS, ex.getCode());
        assertEquals(Map.of("email", "test@test.com"), ex.getDetails());

        verify(userRepository).existsByEmail("test@test.com");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Сохраняет нового пользователя и возвращает id и нормализованный email")
    void savesNewUserAndReturnsIdAndNormalizedEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        RegisterUserUseCase useCase = new RegisterUserUseCase(userRepository);

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(
                User.create(
                        10L,
                        "Doe",
                        "John",
                        null,
                        "test@test.com",
                        "Password123!",
                        "+79991234567"
                )
        );

        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "  Doe  ",
                "  John  ",
                "   ",
                "  Test@Test.com  ",
                "Password123!",
                "+7 999-123-45-67"
        );

        RegisterUserUseCase.Result result = useCase.execute(command);

        assertEquals(10L, result.userId());
        assertEquals("test@test.com", result.email());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).existsByEmail("test@test.com");
        verify(userRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
        assertEquals("Doe", captor.getValue().getSurname());
        assertEquals("John", captor.getValue().getFirstName());
        assertNull(captor.getValue().getSecondName());
        assertEquals("test@test.com", captor.getValue().getEmail().value());
        assertEquals("+79991234567", captor.getValue().getPhone().value());

        verifyNoMoreInteractions(userRepository);
    }
}

