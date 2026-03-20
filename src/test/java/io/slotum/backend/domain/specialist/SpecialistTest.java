package io.slotum.backend.domain.specialist;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialistTest {

    @Test
    @DisplayName("Корректное создание специалиста с валидными данными")
    void createsSpecialistWithValidData() {
        Specialist specialist = Specialist.create(10L, "Some description", 4.5);

        assertEquals(10L, specialist.getUserId());
        assertEquals("Some description", specialist.getDescription());
        assertEquals(4.5, specialist.getGrade());
    }

    @Test
    @DisplayName("userId может быть null")
    void allowsNullUserId() {
        Specialist specialist = Specialist.create(null, "Some description", 4.5);
        assertNull(specialist.getUserId());
    }

    @Test
    @DisplayName("userId равный 0 недопустим")
    void rejectsZeroUserId() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(0L, "Some description", 4.5));
        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Отрицательный userId недопустим")
    void rejectsNegativeUserId() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(-1L, "Some description", 4.5));
        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Описание обязательно (null недопустим)")
    void rejectsNullDescription() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, null, 4.5));
        assertEquals(ErrorCode.EMPTY_SPECIALIST_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("Описание обязательно (пустая строка недопустима)")
    void rejectsEmptyDescription() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, "", 4.5));
        assertEquals(ErrorCode.EMPTY_SPECIALIST_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("Описание из пробелов допустимо (isEmpty, не isBlank)")
    void allowsWhitespaceOnlyDescription() {
        Specialist specialist = Specialist.create(10L, "   ", 4.5);
        assertEquals("   ", specialist.getDescription());
    }

    @Test
    @DisplayName("Описание длиной 1024 символа допустимо")
    void allowsDescriptionLength1024() {
        String description = "a".repeat(1024);
        Specialist specialist = Specialist.create(10L, description, 4.5);
        assertEquals(1024, specialist.getDescription().length());
    }

    @Test
    @DisplayName("Описание длиной 1025 символов недопустимо")
    void rejectsDescriptionLength1025() {
        String description = "a".repeat(1025);
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, description, 4.5));
        assertEquals(ErrorCode.TOO_LONG_SPECIALIST_DESCRIPTION, ex.getCode());
        assertEquals(description, ex.getDetails().get("description"));
    }

    @Test
    @DisplayName("Граничные значения grade (0 и 5) допустимы")
    void allowsGradeBoundaries() {
        Specialist low = Specialist.create(10L, "Some description", 0.0);
        Specialist high = Specialist.create(10L, "Some description", 5.0);

        assertEquals(0.0, low.getGrade());
        assertEquals(5.0, high.getGrade());
    }

    @Test
    @DisplayName("grade меньше 0 недопустим")
    void rejectsGradeBelowZero() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, "Some description", -0.1));
        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(-0.1, (Double) ex.getDetails().get("grade"));
    }

    @Test
    @DisplayName("grade больше 5 недопустим")
    void rejectsGradeAboveFive() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, "Some description", 5.1));
        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
        assertEquals(5.1, (Double) ex.getDetails().get("grade"));
    }

    @Test
    @DisplayName("grade равный null недопустим")
    void rejectsNullGrade() {
        AppException ex = assertThrows(AppException.class, () -> Specialist.create(10L, "Some description", null));
        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
    }
}

