package io.slotum.backend.domain.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class RoleTest {

    /* Создание с полным набором данных */
    @Test
    @DisplayName("Полное создание роли")
    void fullCreation() {
        Role role = Role.create(RoleNames.ADMIN, "Administrator role");

        assertNull(role.getId());
        assertEquals(RoleNames.ADMIN, role.getName());
        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    @DisplayName("Создание роли с явным указанием идентификатора")
    void creationWithId() {
        Role role = Role.restore(1L, RoleNames.ADMIN, "Administrator role");

        assertEquals(1L, role.getId());
        assertEquals(RoleNames.ADMIN, role.getName());
        assertEquals("Administrator role", role.getDescription());
    }

    /* Создание без опциональных полей */
    @Test
    @DisplayName("Создание роли без описания")
    void creationWithoutDescription() {
        Role role = Role.create(RoleNames.USER);

        assertNull(role.getId());
        assertEquals(RoleNames.USER, role.getName());
        assertNull(role.getDescription());
    }

    /* Нормализация входных данных */
    @Test
    @DisplayName("Нормализация описания роли")
    void descriptionNormalization() {
        Role role = Role.create(RoleNames.ADMIN, "  Administrator role  ");

        assertEquals("Administrator role", role.getDescription());
    }

    @Test
    @DisplayName("Создание роли с пустым описанием")
    void creationWithBlankDescription() {
        Role role = Role.create(RoleNames.ADMIN, "   ");

        assertNull(role.getDescription());
    }
}