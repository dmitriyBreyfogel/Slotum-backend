package io.slotum.backend.infrastructure.jpa.repositories.resource;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.infrastructure.jpa.entities.ResourceJpa;
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
public class ResourceRepositoryJpaAdapterTest {

    private static final Long ID = 1L;
    private static final String HTTP_METHOD = "GET";
    private static final String URL_PATTERN = "/api/v1/users";

    @Mock
    private ResourceJpaRepository jpaRepository;

    @InjectMocks
    private ResourceRepositoryJpaAdapter adapter;

    /* Поиск по id */
    @Test
    @DisplayName("Поиск ресурса по id")
    void findById() {
        ResourceJpa jpa = new ResourceJpa(ID, HTTP_METHOD, URL_PATTERN, "Description");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Resource> found = adapter.findById(ID);

        assertTrue(found.isPresent());
        assertEquals(ID, found.get().getId());
        assertEquals(HTTP_METHOD, found.get().getHttpMethod());
        assertEquals(URL_PATTERN, found.get().getUrlPattern());
        assertEquals("Description", found.get().getDescription());
    }

    @Test
    @DisplayName("Поиск несуществующего ресурса по id")
    void findByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Resource> found = adapter.findById(ID);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск ресурса по null id")
    void findByIdNull() {
        when(jpaRepository.findById(null)).thenReturn(Optional.empty());

        Optional<Resource> found = adapter.findById(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск всех */
    @Test
    @DisplayName("Поиск всех ресурсов")
    void findAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                new ResourceJpa(1L, "GET", "/api/v1/users", "Description 1"),
                new ResourceJpa(2L, "POST", "/api/v1/users", "Description 2")
        ));

        List<Resource> resources = adapter.findAll();

        assertEquals(2, resources.size());
    }

    @Test
    @DisplayName("Поиск всех ресурсов при пустой базе")
    void findAllEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<Resource> resources = adapter.findAll();

        assertTrue(resources.isEmpty());
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение нового ресурса")
    void saveNewResource() {
        Resource resource = Resource.create(HTTP_METHOD, URL_PATTERN, "Description");
        ResourceJpa savedJpa = new ResourceJpa(ID, HTTP_METHOD, URL_PATTERN, "Description");

        when(jpaRepository.save(any(ResourceJpa.class))).thenReturn(savedJpa);

        Resource saved = adapter.save(resource);

        assertNotNull(saved.getId());
        assertEquals(HTTP_METHOD, saved.getHttpMethod());
        assertEquals(URL_PATTERN, saved.getUrlPattern());
        assertEquals("Description", saved.getDescription());
        verify(jpaRepository, times(1)).save(any(ResourceJpa.class));
    }

    @Test
    @DisplayName("Сохранение ресурса без описания")
    void saveResourceWithoutDescription() {
        Resource resource = Resource.create(HTTP_METHOD, URL_PATTERN);
        ResourceJpa savedJpa = new ResourceJpa(ID, HTTP_METHOD, URL_PATTERN, null);

        when(jpaRepository.save(any(ResourceJpa.class))).thenReturn(savedJpa);

        Resource saved = adapter.save(resource);

        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление ресурса по id")
    void deleteById() {
        ResourceJpa jpa = new ResourceJpa(ID, HTTP_METHOD, URL_PATTERN, "Description");
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<Resource> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isPresent());
        assertEquals(ID, deleted.get().getId());
        verify(jpaRepository, times(1)).delete(jpa);
    }

    @Test
    @DisplayName("Удаление несуществующего ресурса")
    void deleteByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<Resource> deleted = adapter.deleteById(ID);

        assertTrue(deleted.isEmpty());
        verify(jpaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Удаление всех ресурсов")
    void deleteAll() {
        adapter.deleteAll();

        verify(jpaRepository, times(1)).deleteAll();
    }
}