package io.slotum.backend.infrastructure.jpa.repositories.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.infrastructure.jpa.entities.PermissionJpa;
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
public class PermissionRepositoryJpaAdapterTest {

    private static final Long ID = 1L;
    private static final String CODE = "CREATE_SLOT";

    @Mock
    private PermissionJpaRepository jpaRepository;

    @InjectMocks
    private PermissionRepositoryJpaAdapter adapter;

    /* Поиск по id */
    @Test
    @DisplayName("Поиск разрешения по id")
    void findById() {
        PermissionJpa jpa = new PermissionJpa(ID, CODE, "Description");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Permission> found = adapter.findById(ID);

        assertTrue(found.isPresent());
        assertEquals(ID, found.get().getId());
        assertEquals(CODE, found.get().getCode());
        assertEquals("Description", found.get().getDescription());
    }

    @Test
    @DisplayName("Поиск несуществующего разрешения по id")
    void findByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Permission> found = adapter.findById(ID);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск разрешения по null id")
    void findByIdNull() {
        when(jpaRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Permission> found = adapter.findById(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск по коду */
    @Test
    @DisplayName("Поиск разрешения по коду")
    void findByCode() {
        PermissionJpa jpa = new PermissionJpa(ID, CODE, "Description");
        when(jpaRepository.findByCode(CODE)).thenReturn(Optional.of(jpa));

        Optional<Permission> found = adapter.findByCode(CODE);

        assertTrue(found.isPresent());
        assertEquals(CODE, found.get().getCode());
    }

    @Test
    @DisplayName("Поиск разрешения по несуществующему коду")
    void findByCodeNotFound() {
        when(jpaRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

        Optional<Permission> found = adapter.findByCode("UNKNOWN");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск разрешения по null коду")
    void findByCodeNull() {
        when(jpaRepository.findByCode(null)).thenReturn(Optional.empty());

        Optional<Permission> found = adapter.findByCode(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск всех */
    @Test
    @DisplayName("Поиск всех разрешений")
    void findAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                new PermissionJpa(1L, "CREATE_SLOT", "Description 1"),
                new PermissionJpa(2L, "DELETE_SLOT", "Description 2")
        ));

        List<Permission> permissions = adapter.findAll();

        assertEquals(2, permissions.size());
    }

    @Test
    @DisplayName("Поиск всех разрешений при пустой базе")
    void findAllEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<Permission> permissions = adapter.findAll();

        assertTrue(permissions.isEmpty());
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение нового разрешения")
    void saveNewPermission() {
        Permission permission = Permission.create(CODE, "Description");
        PermissionJpa savedJpa = new PermissionJpa(ID, CODE, "Description");

        when(jpaRepository.save(any(PermissionJpa.class))).thenReturn(savedJpa);

        Permission saved = adapter.save(permission);

        assertNotNull(saved.getId());
        assertEquals(CODE, saved.getCode());
        assertEquals("Description", saved.getDescription());
        verify(jpaRepository, times(1)).save(any(PermissionJpa.class));
    }

    @Test
    @DisplayName("Сохранение разрешения без описания")
    void savePermissionWithoutDescription() {
        Permission permission = Permission.create(CODE);
        PermissionJpa savedJpa = new PermissionJpa(ID, CODE, null);

        when(jpaRepository.save(any(PermissionJpa.class))).thenReturn(savedJpa);

        Permission saved = adapter.save(permission);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление разрешения по id")
    void deleteById() {
        PermissionJpa jpa = new PermissionJpa(ID, CODE, "Description");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Permission> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isPresent());
        assertEquals(ID, deleted.get().getId());
        verify(jpaRepository, times(1)).delete(jpa);
    }

    @Test
    @DisplayName("Удаление несуществующего разрешения")
    void deleteByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Permission> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isEmpty());
        verify(jpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Удаление всех разрешений")
    void deleteAll() {
        adapter.deleteAll();

        verify(jpaRepository, times(1)).deleteAll();
    }
}