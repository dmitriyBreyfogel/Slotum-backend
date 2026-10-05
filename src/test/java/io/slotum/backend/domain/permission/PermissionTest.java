package io.slotum.backend.domain.permission;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class PermissionTest {

    /* Создание с полным набором данных */
    @Test
    @DisplayName("Полное создание разрешения")
    void fullCreation() {
        Permission permission = Permission.create("CREATE_SLOT", "Can create slots");

        assertNull(permission.getId());
        assertEquals("CREATE_SLOT", permission.getCode());
        assertEquals("Can create slots", permission.getDescription());
    }

    @Test
    @DisplayName("Создание разрешения с явным указанием идентификатора")
    void creationWithId() {
        Permission permission = Permission.restore(1L, "CREATE_SLOT", "Can create slots");

        assertEquals(1L, permission.getId());
        assertEquals("CREATE_SLOT", permission.getCode());
        assertEquals("Can create slots", permission.getDescription());
    }

    /* Создание без опциональных полей */
    @Test
    @DisplayName("Создание разрешения без описания")
    void creationWithoutDescription() {
        Permission permission = Permission.create("CREATE_SLOT");

        assertNull(permission.getId());
        assertEquals("CREATE_SLOT", permission.getCode());
        assertNull(permission.getDescription());
    }

    /* Нормализация входных данных */
    @Test
    @DisplayName("Нормализация кода разрешения")
    void codeNormalization() {
        Permission permission = Permission.create("  CREATE_SLOT  ", "Description");

        assertEquals("CREATE_SLOT", permission.getCode());
    }

    @Test
    @DisplayName("Нормализация описания разрешения")
    void descriptionNormalization() {
        Permission permission = Permission.create("CREATE_SLOT", "  Description  ");

        assertEquals("Description", permission.getDescription());
    }

    @Test
    @DisplayName("Создание разрешения с пустым кодом")
    void creationWithBlankCode() {
        Permission permission = Permission.create("   ", "Description");

        assertNull(permission.getCode());
    }
}