package io.slotum.backend.domain.specialist;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class SpecialistTest {

    /* Создание */
    @Test
    @DisplayName("Полное создание специалиста")
    void fullCreation() {
        Specialist specialist = Specialist.create(1L, "Description", 4.5);

        assertEquals(1L, specialist.getUserId());
        assertEquals("Description", specialist.getDescription());
        assertEquals(4.5, specialist.getGrade());
    }

    @Test
    @DisplayName("Создание специалиста с оценкой на нижней границе")
    void creationWithZeroGrade() {
        Specialist specialist = Specialist.create(1L, "Description", 0.0);

        assertEquals(0.0, specialist.getGrade());
    }

    @Test
    @DisplayName("Создание специалиста с оценкой на верхней границе")
    void creationWithFiveGrade() {
        Specialist specialist = Specialist.create(1L, "Description", 5.0);

        assertEquals(5.0, specialist.getGrade());
    }

    @Test
    @DisplayName("Создание специалиста без оценки")
    void creationWithoutGrade() {
        Specialist specialist = Specialist.create(1L, "Description", null);

        assertNull(specialist.getGrade());
    }

    @Test
    @DisplayName("Создание специалиста без описания")
    void creationWithoutDescription() {
        Specialist specialist = Specialist.create(1L, null, 4.5);

        assertNull(specialist.getDescription());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация описания специалиста")
    void descriptionNormalization() {
        Specialist specialist = Specialist.create(1L, "  Description  ", 4.5);

        assertEquals("Description", specialist.getDescription());
    }

    @Test
    @DisplayName("Создание специалиста с пустым описанием")
    void creationWithBlankDescription() {
        Specialist specialist = Specialist.create(1L, "   ", 4.5);

        assertNull(specialist.getDescription());
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание специалиста с оценкой меньше нуля")
    void creationWithNegativeGrade() {
        AppException ex = assertThrows(AppException.class, () ->
                Specialist.create(1L, "Description", -0.1)
        );

        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(Map.of("grade", -0.1), ex.getDetails());
    }

    @Test
    @DisplayName("Создание специалиста с оценкой больше пяти")
    void creationWithGradeHigherThanFive() {
        AppException ex = assertThrows(AppException.class, () ->
                Specialist.create(1L, "Description", 5.1)
        );

        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(Map.of("grade", 5.1), ex.getDetails());
    }
}