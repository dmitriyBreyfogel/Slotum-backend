package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizationRepositoryJpaAdapterTest {

    @Mock
    private OrganizationJpaRepository jpaRepository;

    @InjectMocks
    private OrganizationRepositoryJpaAdapter adapter;

    /* Поиск по id */
    @Test
    @DisplayName("Поиск организации по id")
    void findById() {
        OrganizationJpa jpa = new OrganizationJpa(1L, "Organization", "Description", 4.5);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(jpa));

        Optional<Organization> found = adapter.findById(1L);

        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        assertEquals("Organization", found.get().getName());
        assertEquals("Description", found.get().getDescription());
        assertEquals(4.5, found.get().getGrade());
    }

    @Test
    @DisplayName("Поиск несуществующей организации по id")
    void findByIdNotFound() {
        when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Organization> found = adapter.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск организации по null id")
    void findByIdNull() {
        when(jpaRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Organization> found = adapter.findById(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск по имени */
    @Test
    @DisplayName("Поиск организации по имени")
    void findByName() {
        OrganizationJpa jpa = new OrganizationJpa(1L, "Organization", "Description", 4.5);
        when(jpaRepository.findByName("Organization")).thenReturn(Optional.of(jpa));

        Optional<Organization> found = adapter.findByName("Organization");

        assertTrue(found.isPresent());
        assertEquals("Organization", found.get().getName());
    }

    @Test
    @DisplayName("Поиск организации по несуществующему имени")
    void findByNameNotFound() {
        when(jpaRepository.findByName("Unknown")).thenReturn(Optional.empty());

        Optional<Organization> found = adapter.findByName("Unknown");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск организации по null имени")
    void findByNameNull() {
        when(jpaRepository.findByName(null)).thenReturn(Optional.empty());

        Optional<Organization> found = adapter.findByName(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск всех */
    @Test
    @DisplayName("Поиск всех организаций")
    void findAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                new OrganizationJpa(1L, "Org1", "Description 1", 4.0),
                new OrganizationJpa(2L, "Org2", "Description 2", 3.0)
        ));

        List<Organization> organizations = adapter.findAll();

        assertEquals(2, organizations.size());
    }

    @Test
    @DisplayName("Поиск всех организаций при пустой базе")
    void findAllEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<Organization> organizations = adapter.findAll();

        assertTrue(organizations.isEmpty());
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение новой организации")
    void saveNewOrganization() {
        Organization organization = Organization.create("Organization", "Description", 4.5);
        OrganizationJpa savedJpa = new OrganizationJpa(1L, "Organization", "Description", 4.5);

        when(jpaRepository.save(any(OrganizationJpa.class))).thenReturn(savedJpa);

        Organization saved = adapter.save(organization);

        assertNotNull(saved.getId());
        assertEquals("Organization", saved.getName());
        assertEquals("Description", saved.getDescription());
        assertEquals(4.5, saved.getGrade());
        verify(jpaRepository, times(1)).save(any(OrganizationJpa.class));
    }

    @Test
    @DisplayName("Сохранение организации без оценки")
    void saveOrganizationWithoutGrade() {
        Organization organization = Organization.create("Organization", "Description");
        OrganizationJpa savedJpa = new OrganizationJpa(1L, "Organization", "Description", null);

        when(jpaRepository.save(any(OrganizationJpa.class))).thenReturn(savedJpa);

        Organization saved = adapter.save(organization);

        assertNotNull(saved.getId());
        assertNull(saved.getGrade());
    }

    @Test
    @DisplayName("Сохранение организации без описания")
    void saveOrganizationWithoutDescription() {
        Organization organization = Organization.create("Organization", 4.5);
        OrganizationJpa savedJpa = new OrganizationJpa(1L, "Organization", null, 4.5);

        when(jpaRepository.save(any(OrganizationJpa.class))).thenReturn(savedJpa);

        Organization saved = adapter.save(organization);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление организации по id")
    void deleteById() {
        OrganizationJpa jpa = new OrganizationJpa(1L, "Organization", "Description", 4.5);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(jpa));

        Optional<Organization> deleted = adapter.deleteById(1L);

        assertTrue(deleted.isPresent());
        assertEquals(1L, deleted.get().getId());
        verify(jpaRepository, times(1)).delete(jpa);
    }

    @Test
    @DisplayName("Удаление несуществующей организации")
    void deleteByIdNotFound() {
        when(jpaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Organization> deleted = adapter.deleteById(999L);

        assertTrue(deleted.isEmpty());
        verify(jpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Удаление всех организаций")
    void deleteAll() {
        adapter.deleteAll();

        verify(jpaRepository, times(1)).deleteAll();
    }
}