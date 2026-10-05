package io.slotum.backend.infrastructure.jpa.repositories.organizationMember;

import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizationMemberRepositoryJpaAdapterTest {

    private static final Long ORGANIZATION_ID = 1L;
    private static final Long SPECIALIST_USER_ID = 2L;

    @Mock
    private OrganizationMemberJpaRepository jpaRepository;

    @InjectMocks
    private OrganizationMemberRepositoryJpaAdapter adapter;

    /* Проверка существования */
    @Test
    @DisplayName("Проверка существования члена организации")
    void existsReturnsTrueWhenMemberExists() {
        when(jpaRepository.existsByIdOrganizationIdAndIdSpecialistId(ORGANIZATION_ID, SPECIALIST_USER_ID))
                .thenReturn(true);

        boolean exists = adapter.exists(ORGANIZATION_ID, SPECIALIST_USER_ID);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Проверка несуществующего члена организации")
    void existsReturnsFalseWhenMemberNotFound() {
        when(jpaRepository.existsByIdOrganizationIdAndIdSpecialistId(ORGANIZATION_ID, SPECIALIST_USER_ID))
                .thenReturn(false);

        boolean exists = adapter.exists(ORGANIZATION_ID, SPECIALIST_USER_ID);

        assertFalse(exists);
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение члена организации")
    void saveReturnsSavedMember() {
        OrganizationMemberJpa savedJpa = new OrganizationMemberJpa(ORGANIZATION_ID, SPECIALIST_USER_ID);
        when(jpaRepository.save(any(OrganizationMemberJpa.class))).thenReturn(savedJpa);

        OrganizationMember saved = adapter.save(ORGANIZATION_ID, SPECIALIST_USER_ID);

        assertNotNull(saved);
        assertEquals(ORGANIZATION_ID, saved.getOrganizationId());
        assertEquals(SPECIALIST_USER_ID, saved.getSpecialistUserId());
        verify(jpaRepository, times(1)).save(any(OrganizationMemberJpa.class));
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление члена организации")
    void deleteReturnsDeletedMember() {
        OrganizationMemberJpa memberJpa = new OrganizationMemberJpa(ORGANIZATION_ID, SPECIALIST_USER_ID);
        when(jpaRepository.findById(any())).thenReturn(Optional.of(memberJpa));

        Optional<OrganizationMember> deleted = adapter.delete(ORGANIZATION_ID, SPECIALIST_USER_ID);

        assertTrue(deleted.isPresent());
        assertEquals(ORGANIZATION_ID, deleted.get().getOrganizationId());
        assertEquals(SPECIALIST_USER_ID, deleted.get().getSpecialistUserId());
        verify(jpaRepository, times(1)).delete(memberJpa);
    }

    @Test
    @DisplayName("Удаление несуществующего члена организации")
    void deleteReturnsEmptyWhenMemberNotFound() {
        when(jpaRepository.findById(any())).thenReturn(Optional.empty());

        Optional<OrganizationMember> deleted = adapter.delete(ORGANIZATION_ID, SPECIALIST_USER_ID);

        assertTrue(deleted.isEmpty());
        verify(jpaRepository, never()).delete(any());
    }
}