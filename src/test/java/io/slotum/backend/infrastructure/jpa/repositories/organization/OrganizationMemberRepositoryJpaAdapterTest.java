package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMember;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class OrganizationMemberRepositoryJpaAdapterTest {

    @Test
    @DisplayName("exists делегирует проверку composite key в JpaRepository")
    void existsDelegatesToJpaRepository() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);

        when(jpaRepository.existsByIdOrganizationIdAndIdSpecialistId(1L, 10L)).thenReturn(true);

        boolean exists = adapter.exists(1L, 10L);

        assertEquals(true, exists);
        verify(jpaRepository).existsByIdOrganizationIdAndIdSpecialistId(1L, 10L);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("save сохраняет OrganizationMemberJpa и возвращает domain")
    void savePersistsJpaAndReturnsDomain() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);

        when(jpaRepository.save(any())).thenReturn(new OrganizationMemberJpa(1L, 10L));

        OrganizationMember result = adapter.save(1L, 10L);

        assertEquals(1L, result.getOrganizationId());
        assertEquals(10L, result.getSpecialistUserId());

        ArgumentCaptor<OrganizationMemberJpa> captor = ArgumentCaptor.forClass(OrganizationMemberJpa.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId().getOrganizationId());
        assertEquals(10L, captor.getValue().getId().getSpecialistId());
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("delete удаляет найденную связь и возвращает domain")
    void deleteRemovesFoundMembershipAndReturnsDomain() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);
        OrganizationMemberJpa.OrganizationMemberId id = new OrganizationMemberJpa.OrganizationMemberId(1L, 10L);

        when(jpaRepository.findById(id)).thenReturn(Optional.of(new OrganizationMemberJpa(1L, 10L)));

        OrganizationMember result = adapter.delete(1L, 10L);

        assertEquals(1L, result.getOrganizationId());
        assertEquals(10L, result.getSpecialistUserId());
        verify(jpaRepository).findById(id);
        verify(jpaRepository).deleteById(id);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("delete возвращает null, если связь не найдена")
    void deleteReturnsNullWhenMembershipNotFound() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);
        OrganizationMemberJpa.OrganizationMemberId id = new OrganizationMemberJpa.OrganizationMemberId(1L, 10L);

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        OrganizationMember result = adapter.delete(1L, 10L);

        assertNull(result);
        verify(jpaRepository).findById(id);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("findSpecialistsByOrganizationId возвращает специалистов организации")
    void findSpecialistsByOrganizationIdMapsSpecialists() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);
        OrganizationMemberJpa member = mock(OrganizationMemberJpa.class);

        when(member.getSpecialist()).thenReturn(new SpecialistJpa(10L, "Some description", 4.5));
        when(jpaRepository.findAllByIdOrganizationId(1L)).thenReturn(List.of(member));

        List<Specialist> result = adapter.findSpecialistsByOrganizationId(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getUserId());
        assertEquals("Some description", result.get(0).getDescription());
        assertEquals(4.5, result.get(0).getGrade());
        verify(jpaRepository).findAllByIdOrganizationId(1L);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("findOrganizationsBySpecialistUserId возвращает организации специалиста")
    void findOrganizationsBySpecialistUserIdMapsOrganizations() {
        OrganizationMemberJpaRepository jpaRepository = mock(OrganizationMemberJpaRepository.class);
        OrganizationMemberRepositoryJpaAdapter adapter = new OrganizationMemberRepositoryJpaAdapter(jpaRepository);
        OrganizationMemberJpa member = mock(OrganizationMemberJpa.class);

        when(member.getOrganization()).thenReturn(new OrganizationJpa(1L, "Org", "Description", 3.5));
        when(jpaRepository.findAllByIdSpecialistId(10L)).thenReturn(List.of(member));

        List<Organization> result = adapter.findOrganizationsBySpecialistUserId(10L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Org", result.get(0).getName());
        assertEquals("Description", result.get(0).getDescription());
        assertEquals(3.5, result.get(0).getGrade());
        verify(jpaRepository).findAllByIdSpecialistId(10L);
        verifyNoMoreInteractions(jpaRepository);
    }
}
