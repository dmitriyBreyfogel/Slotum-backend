package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

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

public class CreateSpecialistUseCaseTest {

    @Test
    @DisplayName("Не обращается к репозиториям, если userId невалиден (<= 0)")
    void rejectsInvalidUserIdBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                0L,
                "Some description",
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, ex.getCode());
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если description = null")
    void rejectsNullDescriptionBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                null,
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.EMPTY_SPECIALIST_DESCRIPTION, ex.getCode());
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если description = empty")
    void rejectsEmptyDescriptionBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "",
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.EMPTY_SPECIALIST_DESCRIPTION, ex.getCode());
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если description слишком длинный (> 1024)")
    void rejectsTooLongDescriptionBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        String description = "a".repeat(1025);
        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                description,
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.TOO_LONG_SPECIALIST_DESCRIPTION, ex.getCode());
        assertEquals(description, ex.getDetails().get("description"));
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если grade = null")
    void rejectsNullGradeBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                null
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если grade вне диапазона (< 0)")
    void rejectsGradeBelowZeroBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                -0.1
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(-0.1, (Double) ex.getDetails().get("grade"));
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиториям, если grade вне диапазона (> 5)")
    void rejectsGradeAboveFiveBeforeRepositories() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                5.1
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(5.1, (Double) ex.getDetails().get("grade"));
        verifyNoInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Бросает исключение USER_NOT_FOUND, если user не найден (и не обращается к SpecialistRepository)")
    void throwsIfUserNotFound() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("userId", 10L), ex.getDetails());

        verify(userRepository).findById(10L);
        verifyNoInteractions(specialistRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Бросает исключение SPECIALIST_ALREADY_EXISTS, если specialist с таким userId уже существует")
    void throwsIfSpecialistAlreadyExists() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        when(userRepository.findById(10L)).thenReturn(Optional.of(mock(User.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.SPECIALIST_ALREADY_EXISTS, ex.getCode());
        assertEquals(Map.of("userId", 10L), ex.getDetails());

        verify(userRepository).findById(10L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(specialistRepository, never()).save(any());
        verifyNoMoreInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Сохраняет нового specialist и возвращает данные из сохранённой сущности")
    void savesNewSpecialistAndReturnsData() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        when(userRepository.findById(10L)).thenReturn(Optional.of(mock(User.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.empty());
        when(specialistRepository.save(any())).thenReturn(Specialist.create(10L, "Saved description", 2.5));

        CreateSpecialistUseCase.Command command = new CreateSpecialistUseCase.Command(
                10L,
                "Some description",
                4.5
        );

        CreateSpecialistUseCase.Result result = useCase.execute(command);

        assertEquals(10L, result.userId());
        assertEquals("Saved description", result.description());
        assertEquals(2.5, result.grade());

        ArgumentCaptor<Specialist> captor = ArgumentCaptor.forClass(Specialist.class);
        verify(userRepository).findById(10L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(specialistRepository).save(captor.capture());

        assertEquals(10L, captor.getValue().getUserId());
        assertEquals("Some description", captor.getValue().getDescription());
        assertEquals(4.5, captor.getValue().getGrade());

        verifyNoMoreInteractions(specialistRepository, userRepository);
    }

    @Test
    @DisplayName("Считает null из findSpecialistByUserId как 'не найден' и всё равно сохраняет")
    void savesWhenFindSpecialistByUserIdReturnsNull() {
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSpecialistUseCase useCase = new CreateSpecialistUseCase(specialistRepository, userRepository);

        when(userRepository.findById(10L)).thenReturn(Optional.of(mock(User.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(null);
        when(specialistRepository.save(any())).thenReturn(Specialist.create(10L, "Saved description", 4.5));

        CreateSpecialistUseCase.Result result = useCase.execute(
                new CreateSpecialistUseCase.Command(10L, "Some description", 4.5)
        );

        assertEquals(10L, result.userId());
        assertEquals("Saved description", result.description());
        assertEquals(4.5, result.grade());

        ArgumentCaptor<Specialist> captor = ArgumentCaptor.forClass(Specialist.class);
        verify(userRepository).findById(10L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(specialistRepository).save(captor.capture());

        assertEquals(10L, captor.getValue().getUserId());
        assertEquals("Some description", captor.getValue().getDescription());
        assertEquals(4.5, captor.getValue().getGrade());
        verifyNoMoreInteractions(specialistRepository, userRepository);
    }
}
