package io.slotum.backend.domain.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class ResourceTest {

    /* Создание с полным набором данных */
    @Test
    @DisplayName("Полное создание ресурса")
    void fullCreation() {
        Resource resource = Resource.create("GET", "/api/v1/users", "Get all users");

        assertNull(resource.getId());
        assertEquals("GET", resource.getHttpMethod());
        assertEquals("/api/v1/users", resource.getUrlPattern());
        assertEquals("Get all users", resource.getDescription());
    }

    @Test
    @DisplayName("Создание ресурса с явным указанием идентификатора")
    void creationWithId() {
        Resource resource = Resource.restore(1L, "POST", "/api/v1/users", "Create user");

        assertEquals(1L, resource.getId());
        assertEquals("POST", resource.getHttpMethod());
        assertEquals("/api/v1/users", resource.getUrlPattern());
        assertEquals("Create user", resource.getDescription());
    }

    /* Создание без опциональных полей */
    @Test
    @DisplayName("Создание ресурса без описания")
    void creationWithoutDescription() {
        Resource resource = Resource.create("DELETE", "/api/v1/users");

        assertNull(resource.getId());
        assertEquals("DELETE", resource.getHttpMethod());
        assertEquals("/api/v1/users", resource.getUrlPattern());
        assertNull(resource.getDescription());
    }

    /* Нормализация входных данных */
    @Test
    @DisplayName("Нормализация HTTP метода")
    void httpMethodNormalization() {
        Resource resource = Resource.create("  GET  ", "/api/v1/users", "Description");

        assertEquals("GET", resource.getHttpMethod());
    }

    @Test
    @DisplayName("Нормализация URL шаблона")
    void urlPatternNormalization() {
        Resource resource = Resource.create("GET", "  /api/v1/users  ", "Description");

        assertEquals("/api/v1/users", resource.getUrlPattern());
    }

    @Test
    @DisplayName("Нормализация описания")
    void descriptionNormalization() {
        Resource resource = Resource.create("GET", "/api/v1/users", "  Description  ");

        assertEquals("Description", resource.getDescription());
    }

    @Test
    @DisplayName("Создание ресурса с пустым HTTP методом")
    void creationWithBlankHttpMethod() {
        Resource resource = Resource.create("   ", "/api/v1/users", "Description");

        assertNull(resource.getHttpMethod());
    }

    @Test
    @DisplayName("Создание ресурса с пустым URL шаблоном")
    void creationWithBlankUrlPattern() {
        Resource resource = Resource.create("GET", "   ", "Description");

        assertNull(resource.getUrlPattern());
    }
}