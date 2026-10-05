package io.slotum.backend.domain.organization;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class OrganizationTest {

    /* Создание с полным набором данных */
    @Test
    @DisplayName("Полное создание организации")
    void fullCreation() {
        Organization org = Organization.create("Organization", "Description", 5.0);

        assertNull(org.getId());
        assertEquals("Organization", org.getName());
        assertEquals("Description", org.getDescription());
        assertEquals(5.0, org.getGrade());
    }

    @Test
    @DisplayName("Создание организации с явным указанием идентификатора")
    void creationWithId() {
        Organization org = Organization.restore(1L, "Organization", "Description", 5.0);

        assertEquals(1L, org.getId());
        assertEquals("Organization", org.getName());
        assertEquals("Description", org.getDescription());
        assertEquals(5.0, org.getGrade());
    }

    /* Создание без опциональных полей */
    @Test
    @DisplayName("Создание организации без явного указания оценки")
    void creationWithoutGrade() {
        Organization org = Organization.create("Organization", "Description");

        assertNull(org.getId());
        assertEquals("Organization", org.getName());
        assertEquals("Description", org.getDescription());
        assertNull(org.getGrade());
    }

    @Test
    @DisplayName("Создание организации без явного указания описания")
    void creationWithoutDescription() {
        Organization org = Organization.create("Organization", 5.0);

        assertNull(org.getId());
        assertEquals("Organization", org.getName());
        assertNull(org.getDescription());
        assertEquals(5.0, org.getGrade());
    }

    @Test
    @DisplayName("Создание организации без явного указания описания и оценки")
    void creationWithoutDescriptionAndGrade() {
        Organization org = Organization.create("Organization");

        assertNull(org.getId());
        assertEquals("Organization", org.getName());
        assertNull(org.getDescription());
        assertNull(org.getGrade());
    }

    /* Нормализация входных данных */
    @Test
    @DisplayName("Нормализация имени организации")
    void nameNormalization() {
        Organization org = Organization.create("  Organization  ", "Description", 5.0);

        assertEquals("Organization", org.getName());
    }

    @Test
    @DisplayName("Нормализация описания организации")
    void descriptionNormalization() {
        Organization org = Organization.create("Organization", "  Description  ", 5.0);

        assertEquals("Description", org.getDescription());
    }

    @Test
    @DisplayName("Создание организации с пустым именем")
    void creationWithBlankName() {
        Organization org = Organization.create("   ", "Description", 5.0);

        assertNull(org.getName());
    }

    /* Пограничные значения оценки */
    @Test
    @DisplayName("Создание организации с оценкой на нижней границе")
    void creationWithZeroGrade() {
        Organization org = Organization.create("Organization", "Description", 0.0);

        assertEquals(0.0, org.getGrade());
    }

    @Test
    @DisplayName("Создание организации с оценкой на верхней границе")
    void creationWithFiveGrade() {
        Organization org = Organization.create("Organization", "Description", 5.0);

        assertEquals(5.0, org.getGrade());
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание организации с оценкой меньше нуля без явного указания идентификатора")
    void creationWithNegativeGradeWithoutId() {
        AppException ex = assertThrows(AppException.class, () ->
                Organization.create("Organization", "Description", -0.1)
        );

        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(Map.of("grade", -0.1), ex.getDetails());
    }

    @Test
    @DisplayName("Создание организации с оценкой больше 5 без явного указания идентификатора")
    void creationWithGradeHigherThanFiveWithoutId() {
        AppException ex = assertThrows(AppException.class, () ->
                Organization.create("Organization", "Description", 5.1)
        );

        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(Map.of("grade", 5.1), ex.getDetails());
    }

    @Test
    @DisplayName("Создание организации с оценкой меньше нуля с явным указанием идентификатора")
    void creationWithNegativeGrade() {
        AppException ex = assertThrows(AppException.class, () ->
                Organization.restore(1L, "Organization", "Description", -0.1)
        );

        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(Map.of("grade", -0.1), ex.getDetails());
    }

    @Test
    @DisplayName("Создание организации с оценкой больше 5 с явным указанием идентификатора")
    void creationWithGradeHigherThanFive() {
        AppException ex = assertThrows(AppException.class, () ->
                Organization.restore(1L, "Organization", "Description", 5.1)
        );

        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
        assertEquals(Map.of("grade", 5.1), ex.getDetails());
    }
}