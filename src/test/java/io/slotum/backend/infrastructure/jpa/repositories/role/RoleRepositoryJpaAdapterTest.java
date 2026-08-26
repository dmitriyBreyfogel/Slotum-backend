package io.slotum.backend.infrastructure.jpa.repositories.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.infrastructure.jpa.entities.RoleJpa;
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
public class RoleRepositoryJpaAdapterTest {

    private static final Long ID = 1L;
    private static final RoleNames NAME = RoleNames.ADMIN;

    @Mock
    private RoleJpaRepository jpaRepository;

    @InjectMocks
    private RoleRepositoryJpaAdapter adapter;

    /* Поиск по id */
    @Test
    @DisplayName("Поиск роли по id")
    void findById() {
        RoleJpa jpa = new RoleJpa(ID, NAME, "Administrator");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Role> found = adapter.findById(ID);

        assertTrue(found.isPresent());
        assertEquals(ID, found.get().getId());
        assertEquals(NAME, found.get().getName());
        assertEquals("Administrator", found.get().getDescription());
    }

    @Test
    @DisplayName("Поиск несуществующей роли по id")
    void findByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Role> found = adapter.findById(ID);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск роли по null id")
    void findByIdNull() {
        when(jpaRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Role> found = adapter.findById(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск по имени */
    @Test
    @DisplayName("Поиск роли по имени")
    void findByName() {
        RoleJpa jpa = new RoleJpa(ID, NAME, "Administrator");
        when(jpaRepository.findByName(NAME)).thenReturn(Optional.of(jpa));

        Optional<Role> found = adapter.findByName(NAME);

        assertTrue(found.isPresent());
        assertEquals(NAME, found.get().getName());
    }

    @Test
    @DisplayName("Поиск роли по несуществующему имени")
    void findByNameNotFound() {
        when(jpaRepository.findByName(RoleNames.USER)).thenReturn(Optional.empty());

        Optional<Role> found = adapter.findByName(RoleNames.USER);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск роли по null имени")
    void findByNameNull() {
        when(jpaRepository.findByName(null)).thenReturn(Optional.empty());

        Optional<Role> found = adapter.findByName(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск всех */
    @Test
    @DisplayName("Поиск всех ролей")
    void findAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                new RoleJpa(1L, RoleNames.ADMIN, "Administrator"),
                new RoleJpa(2L, RoleNames.USER, "User")
        ));

        List<Role> roles = adapter.findAll();

        assertEquals(2, roles.size());
    }

    @Test
    @DisplayName("Поиск всех ролей при пустой базе")
    void findAllEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<Role> roles = adapter.findAll();

        assertTrue(roles.isEmpty());
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение новой роли")
    void saveNewRole() {
        Role role = Role.create(NAME, "Administrator");
        RoleJpa savedJpa = new RoleJpa(ID, NAME, "Administrator");

        when(jpaRepository.save(any(RoleJpa.class))).thenReturn(savedJpa);

        Role saved = adapter.save(role);

        assertNotNull(saved.getId());
        assertEquals(NAME, saved.getName());
        assertEquals("Administrator", saved.getDescription());
        verify(jpaRepository, times(1)).save(any(RoleJpa.class));
    }

    @Test
    @DisplayName("Сохранение роли без описания")
    void saveRoleWithoutDescription() {
        Role role = Role.create(NAME);
        RoleJpa savedJpa = new RoleJpa(ID, NAME, null);

        when(jpaRepository.save(any(RoleJpa.class))).thenReturn(savedJpa);

        Role saved = adapter.save(role);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление роли по id")
    void deleteById() {
        RoleJpa jpa = new RoleJpa(ID, NAME, "Administrator");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Role> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isPresent());
        assertEquals(ID, deleted.get().getId());
        verify(jpaRepository, times(1)).delete(jpa);
    }

    @Test
    @DisplayName("Удаление несуществующей роли")
    void deleteByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Role> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isEmpty());
        verify(jpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Удаление всех ролей")
    void deleteAll() {
        adapter.deleteAll();

        verify(jpaRepository, times(1)).deleteAll();
    }
}