package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateOrganizationUseCaseTest {

    @Test
    @DisplayName("Бросает исключение, если организация с таким именем уже существует (проверка по trim-имени)")
    void throwsIfOrganizationWithSameNameAlreadyExists() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        when(organizationRepository.findByName("Org"))
                .thenReturn(Optional.of(Organization.create(1L, "Org", "Existing")));

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command("  Org  ", "New"))
        );

        assertEquals(ErrorCode.ORGANIZATION_ALREADY_EXISTS, ex.getCode());
        assertEquals(Map.of("name", "Org"), ex.getDetails());

        verify(organizationRepository).findByName("Org");
        verify(organizationRepository, never()).save(any());
        verifyNoMoreInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Сохраняет новую организацию и возвращает id и нормализованное имя")
    void savesNewOrganizationAndReturnsIdAndName() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        when(organizationRepository.findByName("Org"))
                .thenReturn(Optional.empty());
        when(organizationRepository.save(any()))
                .thenReturn(Organization.create(10L, "Org", "Desc"));

        Organization result = useCase.execute(
                new CreateOrganizationUseCase.Command("  Org  ", "Desc")
        );

        assertEquals(10L, result.getId());
        assertEquals("Org", result.getName());

        ArgumentCaptor<Organization> captor = ArgumentCaptor.forClass(Organization.class);
        verify(organizationRepository).findByName("Org");
        verify(organizationRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
        assertEquals("Org", captor.getValue().getName());
        assertEquals("Desc", captor.getValue().getDescription());
        assertEquals(0.0, captor.getValue().getGrade());
        verifyNoMoreInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует имя: null недопустим (до обращения к репозиторию)")
    void rejectsNullName() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command(null, "Desc"))
        );

        assertEquals(ErrorCode.EMPTY_ORGANIZATION_NAME, ex.getCode());
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует имя: пустая строка недопустима (до обращения к репозиторию)")
    void rejectsEmptyName() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command("", "Desc"))
        );

        assertEquals(ErrorCode.EMPTY_ORGANIZATION_NAME, ex.getCode());
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует имя: длина > 255 недопустима (до обращения к репозиторию)")
    void rejectsTooLongName() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        String name = "a".repeat(256);
        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command(name, "Desc"))
        );

        assertEquals(ErrorCode.TOO_LONG_ORGANIZATION_NAME, ex.getCode());
        assertEquals(name, ex.getDetails().get("name"));
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует описание: null недопустимо (до обращения к репозиторию)")
    void rejectsNullDescription() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command("Org", null))
        );

        assertEquals(ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION, ex.getCode());
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует описание: пустая строка недопустима (до обращения к репозиторию)")
    void rejectsEmptyDescription() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command("Org", ""))
        );

        assertEquals(ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION, ex.getCode());
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Валидирует описание: длина > 1024 недопустима (до обращения к репозиторию)")
    void rejectsTooLongDescription() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        String description = "a".repeat(1025);
        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateOrganizationUseCase.Command("Org", description))
        );

        assertEquals(ErrorCode.TOO_LONG_ORGANIZATION_DESCRIPTION, ex.getCode());
        assertEquals(description, ex.getDetails().get("description"));
        verifyNoInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Граничные значения: name(255) и description(1024) допустимы")
    void allowsMaxLengths() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateOrganizationUseCase useCase = new CreateOrganizationUseCase(organizationRepository);

        String name = "a".repeat(255);
        String description = "b".repeat(1024);

        when(organizationRepository.findByName(name))
                .thenReturn(Optional.empty());
        when(organizationRepository.save(any()))
                .thenReturn(Organization.create(1L, name, description));

        Organization result = useCase.execute(
                new CreateOrganizationUseCase.Command(name, description)
        );

        assertEquals(1L, result.getId());
        assertEquals(name, result.getName());

        verify(organizationRepository).findByName(name);
        verify(organizationRepository).save(any());
        verifyNoMoreInteractions(organizationRepository);
    }
}

