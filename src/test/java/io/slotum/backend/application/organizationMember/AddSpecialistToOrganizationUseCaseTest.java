package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMember;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AddSpecialistToOrganizationUseCaseTest {

    @Test
    @DisplayName("Не обращается к репозиториям, если organizationId невалиден")
    void rejectsInvalidOrganizationIdBeforeRepositories() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        AddSpecialistToOrganizationUseCase useCase = new AddSpecialistToOrganizationUseCase(
                organizationMemberRepository,
                organizationRepository,
                specialistRepository
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(0L, 10L));

        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, ex.getCode());
        verifyNoInteractions(organizationMemberRepository, organizationRepository, specialistRepository);
    }

    @Test
    @DisplayName("Бросает ORGANIZATION_NOT_FOUND, если организация не найдена")
    void throwsIfOrganizationNotFound() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        AddSpecialistToOrganizationUseCase useCase = new AddSpecialistToOrganizationUseCase(
                organizationMemberRepository,
                organizationRepository,
                specialistRepository
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.ORGANIZATION_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("organizationId", 1L), ex.getDetails());
        verify(organizationRepository).findById(1L);
        verifyNoInteractions(organizationMemberRepository, specialistRepository);
        verifyNoMoreInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Бросает SPECIALIST_NOT_FOUND, если специалист не найден")
    void throwsIfSpecialistNotFound() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        AddSpecialistToOrganizationUseCase useCase = new AddSpecialistToOrganizationUseCase(
                organizationMemberRepository,
                organizationRepository,
                specialistRepository
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(mock(Organization.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.SPECIALIST_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("specialistUserId", 10L), ex.getDetails());
        verify(organizationRepository).findById(1L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verifyNoInteractions(organizationMemberRepository);
        verifyNoMoreInteractions(organizationRepository, specialistRepository);
    }

    @Test
    @DisplayName("Бросает ORGANIZATION_MEMBERSHIP_ALREADY_EXISTS, если связь уже есть")
    void throwsIfMembershipAlreadyExists() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        AddSpecialistToOrganizationUseCase useCase = new AddSpecialistToOrganizationUseCase(
                organizationMemberRepository,
                organizationRepository,
                specialistRepository
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(mock(Organization.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(organizationMemberRepository.exists(1L, 10L)).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.ORGANIZATION_MEMBERSHIP_ALREADY_EXISTS, ex.getCode());
        assertEquals(Map.of("organizationId", 1L, "specialistUserId", 10L), ex.getDetails());
        verify(organizationRepository).findById(1L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationMemberRepository).exists(1L, 10L);
        verify(organizationMemberRepository, never()).save(1L, 10L);
        verifyNoMoreInteractions(organizationMemberRepository, organizationRepository, specialistRepository);
    }

    @Test
    @DisplayName("Сохраняет новую связь организации и специалиста")
    void savesNewMembership() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        AddSpecialistToOrganizationUseCase useCase = new AddSpecialistToOrganizationUseCase(
                organizationMemberRepository,
                organizationRepository,
                specialistRepository
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(mock(Organization.class)));
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(organizationMemberRepository.exists(1L, 10L)).thenReturn(false);
        when(organizationMemberRepository.save(1L, 10L)).thenReturn(OrganizationMember.create(1L, 10L));

        OrganizationMember result = useCase.execute(1L, 10L);

        assertEquals(1L, result.getOrganizationId());
        assertEquals(10L, result.getSpecialistUserId());
        verify(organizationRepository).findById(1L);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationMemberRepository).exists(1L, 10L);
        verify(organizationMemberRepository).save(1L, 10L);
        verifyNoMoreInteractions(organizationMemberRepository, organizationRepository, specialistRepository);
    }
}
