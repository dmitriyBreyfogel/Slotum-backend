package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
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

public class CreateMyOrganizationUseCaseTest {

    @Test
    @DisplayName("Создает организацию и сразу привязывает к ней текущего специалиста")
    void createsOrganizationAndMembership() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        CreateMyOrganizationUseCase useCase = new CreateMyOrganizationUseCase(
                organizationRepository,
                organizationMemberRepository,
                specialistRepository
        );

        when(organizationRepository.findByName("Org")).thenReturn(Optional.empty());
        when(specialistRepository.findSpecialistByUserId(10L))
                .thenReturn(Optional.of(Specialist.create(10L, "Spec", 0.0)));
        when(organizationRepository.save(any()))
                .thenReturn(Organization.create(7L, "Org", "Desc"));

        CreateMyOrganizationUseCase.Result result = useCase.execute(
                new CreateMyOrganizationUseCase.Command(10L, "  Org  ", "Desc")
        );

        assertEquals(7L, result.id());
        assertEquals("Org", result.name());
        assertEquals("Desc", result.description());
        assertEquals(0.0, result.grade());

        ArgumentCaptor<Organization> organizationCaptor = ArgumentCaptor.forClass(Organization.class);
        verify(organizationRepository).findByName("Org");
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationRepository).save(organizationCaptor.capture());
        verify(organizationMemberRepository).save(7L, 10L);

        assertNull(organizationCaptor.getValue().getId());
        assertEquals("Org", organizationCaptor.getValue().getName());
        assertEquals("Desc", organizationCaptor.getValue().getDescription());
        verifyNoMoreInteractions(organizationRepository, organizationMemberRepository, specialistRepository);
    }

    @Test
    @DisplayName("Не создает организацию и связь, если организация с таким именем уже есть")
    void throwsIfOrganizationAlreadyExists() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        CreateMyOrganizationUseCase useCase = new CreateMyOrganizationUseCase(
                organizationRepository,
                organizationMemberRepository,
                specialistRepository
        );

        when(organizationRepository.findByName("Org"))
                .thenReturn(Optional.of(Organization.create(1L, "Org", "Existing")));

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateMyOrganizationUseCase.Command(10L, "  Org  ", "Desc"))
        );

        assertEquals(ErrorCode.ORGANIZATION_ALREADY_EXISTS, ex.getCode());
        assertEquals(Map.of("name", "Org"), ex.getDetails());
        verify(organizationRepository).findByName("Org");
        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMemberRepository, specialistRepository);
        verifyNoMoreInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Не создает организацию и связь, если текущий пользователь не специалист")
    void throwsIfCurrentUserIsNotSpecialist() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        CreateMyOrganizationUseCase useCase = new CreateMyOrganizationUseCase(
                organizationRepository,
                organizationMemberRepository,
                specialistRepository
        );

        when(organizationRepository.findByName("Org")).thenReturn(Optional.empty());
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateMyOrganizationUseCase.Command(10L, "Org", "Desc"))
        );

        assertEquals(ErrorCode.SPECIALIST_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("specialistUserId", 10L), ex.getDetails());
        verify(organizationRepository).findByName("Org");
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationRepository, never()).save(any());
        verifyNoInteractions(organizationMemberRepository);
        verifyNoMoreInteractions(organizationRepository, specialistRepository);
    }

    @Test
    @DisplayName("Валидирует организацию до обращения к репозиториям")
    void validatesOrganizationBeforeRepositories() {
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        CreateMyOrganizationUseCase useCase = new CreateMyOrganizationUseCase(
                organizationRepository,
                organizationMemberRepository,
                specialistRepository
        );

        AppException ex = assertThrows(AppException.class, () ->
                useCase.execute(new CreateMyOrganizationUseCase.Command(10L, null, "Desc"))
        );

        assertEquals(ErrorCode.EMPTY_ORGANIZATION_NAME, ex.getCode());
        verifyNoInteractions(organizationRepository, organizationMemberRepository, specialistRepository);
    }
}
