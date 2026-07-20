package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
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

public class GetOrganizationSpecialistsUseCaseTest {

    @Test
    @DisplayName("Бросает ORGANIZATION_NOT_FOUND, если организация не найдена")
    void throwsIfOrganizationNotFound() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        GetOrganizationSpecialistsUseCase useCase = new GetOrganizationSpecialistsUseCase(
                organizationMemberRepository,
                organizationRepository
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L));

        assertEquals(ErrorCode.ORGANIZATION_NOT_FOUND, ex.getCode());
        assertEquals(Map.of("organizationId", 1L), ex.getDetails());
        verify(organizationRepository).findById(1L);
        verifyNoInteractions(organizationMemberRepository);
        verifyNoMoreInteractions(organizationRepository);
    }

    @Test
    @DisplayName("Возвращает специалистов организации")
    void returnsOrganizationSpecialists() {
        OrganizationMemberRepository organizationMemberRepository = mock(OrganizationMemberRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        GetOrganizationSpecialistsUseCase useCase = new GetOrganizationSpecialistsUseCase(
                organizationMemberRepository,
                organizationRepository
        );

        List<Specialist> specialists = List.of(
                Specialist.create(10L, "First", 4.5),
                Specialist.create(11L, "Second", 3.5)
        );

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(mock(Organization.class)));
        when(organizationMemberRepository.findSpecialistsByOrganizationId(1L)).thenReturn(specialists);

        List<Specialist> result = useCase.execute(1L);

        assertSame(specialists, result);
        verify(organizationRepository).findById(1L);
        verify(organizationMemberRepository).findSpecialistsByOrganizationId(1L);
        verifyNoMoreInteractions(organizationMemberRepository, organizationRepository);
    }
}
