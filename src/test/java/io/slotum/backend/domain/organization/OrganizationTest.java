package io.slotum.backend.domain.organization;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationTest {

    @Test
    @DisplayName("Корректное создание организации через create без id")
    void createsOrganizationWithoutId() {
        Organization organization = Organization.create("Org", "Some description", 4.5);

        assertNull(organization.getId());
        assertEquals("Org", organization.getName());
        assertEquals("Some description", organization.getDescription());
        assertEquals(4.5, organization.getGrade());
    }

    @Test
    @DisplayName("Корректное создание организации через create c id (grade по умолчанию 0)")
    void createsOrganizationWithIdAndDefaultGrade() {
        Organization organization = Organization.create(10L, "Org", "Some description");

        assertEquals(10L, organization.getId());
        assertEquals("Org", organization.getName());
        assertEquals("Some description", organization.getDescription());
        assertEquals(0.0, organization.getGrade());
    }

    @Test
    @DisplayName("restore нормализует имя организации (trim)")
    void restoreTrimsOrganizationName() {
        Organization organization = Organization.restore(10L, "  Org  ", "Some description", 4.5);
        assertEquals("Org", organization.getName());
    }

    @Test
    @DisplayName("ID может быть null")
    void allowsNullId() {
        Organization organization = Organization.restore(null, "Org", "Some description", 4.5);
        assertNull(organization.getId());
    }

    @Test
    @DisplayName("ID равный 0 недопустим")
    void rejectsZeroId() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(0L, "Org", "Some description", 4.5));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Отрицательный ID недопустим")
    void rejectsNegativeId() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(-1L, "Org", "Some description", 4.5));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Название организации обязательно (null недопустим)")
    void rejectsNullName() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, null, "Some description", 4.5));
        assertEquals(ErrorCode.EMPTY_ORGANIZATION_NAME, ex.getCode());
    }

    @Test
    @DisplayName("Название организации обязательно (пустая строка недопустима)")
    void rejectsEmptyName() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "", "Some description", 4.5));
        assertEquals(ErrorCode.EMPTY_ORGANIZATION_NAME, ex.getCode());
    }

    @Test
    @DisplayName("Название организации длиной 255 символов допустимо")
    void allowsNameLength255() {
        String name = "a".repeat(255);
        Organization organization = Organization.restore(10L, name, "Some description", 4.5);
        assertEquals(255, organization.getName().length());
    }

    @Test
    @DisplayName("Название организации длиной 256 символов недопустимо")
    void rejectsNameLength256() {
        String name = "a".repeat(256);
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, name, "Some description", 4.5));
        assertEquals(ErrorCode.TOO_LONG_ORGANIZATION_NAME, ex.getCode());
        assertEquals(name, ex.getDetails().get("name"));
    }

    @Test
    @DisplayName("Описание организации обязательно (null недопустим)")
    void rejectsNullDescription() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", null, 4.5));
        assertEquals(ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("Описание организации обязательно (пустая строка недопустима)")
    void rejectsEmptyDescription() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", "", 4.5));
        assertEquals(ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("Описание длиной 1024 символа допустимо")
    void allowsDescriptionLength1024() {
        String description = "a".repeat(1024);
        Organization organization = Organization.restore(10L, "Org", description, 4.5);
        assertEquals(1024, organization.getDescription().length());
    }

    @Test
    @DisplayName("Описание длиной 1025 символов недопустимо")
    void rejectsDescriptionLength1025() {
        String description = "a".repeat(1025);
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", description, 4.5));
        assertEquals(ErrorCode.TOO_LONG_ORGANIZATION_DESCRIPTION, ex.getCode());
        assertEquals(description, ex.getDetails().get("description"));
    }

    @Test
    @DisplayName("Описание сохраняется как есть (без нормализации)")
    void keepsDescriptionAsIs() {
        Organization organization = Organization.restore(10L, "Org", "  Some description  ", 4.5);
        assertEquals("  Some description  ", organization.getDescription());
    }

    @Test
    @DisplayName("Граничные значения grade (0 и 5) допустимы")
    void allowsGradeBoundaries() {
        Organization low = Organization.restore(10L, "Org", "Some description", 0.0);
        Organization high = Organization.restore(10L, "Org", "Some description", 5.0);

        assertEquals(0.0, low.getGrade());
        assertEquals(5.0, high.getGrade());
    }

    @Test
    @DisplayName("grade меньше 0 недопустим")
    void rejectsGradeBelowZero() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", "Some description", -0.1));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(-0.1, (Double) ex.getDetails().get("grade"));
    }

    @Test
    @DisplayName("grade больше 5 недопустим")
    void rejectsGradeAboveFive() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", "Some description", 5.1));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(5.1, (Double) ex.getDetails().get("grade"));
    }

    @Test
    @DisplayName("grade равный null недопустим")
    void rejectsNullGrade() {
        AppException ex = assertThrows(AppException.class, () -> Organization.restore(10L, "Org", "Some description", null));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
    }
}

