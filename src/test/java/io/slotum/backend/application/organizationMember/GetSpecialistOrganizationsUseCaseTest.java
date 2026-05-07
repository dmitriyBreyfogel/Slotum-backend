package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetSpecialistOrganizationsUseCaseTest {

    @Test
    @DisplayName("Бросает SPECIALIST_NOT_FOUND, если специалист не найден")
    void throwsIfSpecialistNotFound() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        GetSpecialistOrganizationsUseCase useCase = new GetSpecialistOrganizationsUseCase(
                organizationMemberRepository,
                specialistRepository
        );

        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(10L));

        assertEquals(ErrorCode.SPECIALIST_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("specialistUserId", 10L), ex.getDetails());
        verify(specialistRepository).findSpecialistByUserId(10L);
        verifyNoInteractions(organizationMemberRepository);
        verifyNoMoreInteractions(specialistRepository);
    }

    @Test
    @DisplayName("Возвращает организации специалиста")
    void returnsSpecialistOrganizations() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        GetSpecialistOrganizationsUseCase useCase = new GetSpecialistOrganizationsUseCase(
                organizationMemberRepository,
                specialistRepository
        );

        List<Organization> organizations = List.of(
                Organization.create(1L, "First org", "First description"),
                Organization.create(2L, "Second org", "Second description")
        );

        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(organizationMemberRepository.findOrganizationsBySpecialistUserId(10L)).thenReturn(organizations);

        List<Organization> result = useCase.execute(10L);

        assertSame(organizations, result);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationMemberRepository).findOrganizationsBySpecialistUserId(10L);
        verifyNoMoreInteractions(organizationMemberRepository, specialistRepository);
    }
}
