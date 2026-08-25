package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class EmailTest {

    /* Создание валидного email */
    @Test
    @DisplayName("Создание email с валидным форматом")
    void creationWithValidEmail() {
        Email email = Email.of("user@example.com");

        assertEquals("user@example.com", email.value());
    }

    @Test
    @DisplayName("Создание email с подчёркиванием и точкой")
    void creationWithUnderscoreAndDot() {
        Email email = Email.of("user.name_1@example.com");

        assertEquals("user.name_1@example.com", email.value());
    }

    @Test
    @DisplayName("Создание email с плюсом")
    void creationWithPlus() {
        Email email = Email.of("user+tag@example.com");

        assertEquals("user+tag@example.com", email.value());
    }

    @Test
    @DisplayName("Создание email с поддоменом")
    void creationWithSubdomain() {
        Email email = Email.of("user@sub.example.com");

        assertEquals("user@sub.example.com", email.value());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация email в нижний регистр")
    void emailLowercaseNormalization() {
        Email email = Email.of("USER@EXAMPLE.COM");

        assertEquals("user@example.com", email.value());
    }

    @Test
    @DisplayName("Нормализация пробелов в email")
    void emailTrimNormalization() {
        Email email = Email.of("  user@example.com  ");

        assertEquals("user@example.com", email.value());
    }

    /* Проверка валидности */
    @Test
    @DisplayName("isValid возвращает true для валидного email")
    void isValidForValidEmail() {
        assertTrue(Email.isValid("user@example.com"));
    }

    @Test
    @DisplayName("isValid возвращает false для null")
    void isValidForNull() {
        assertFalse(Email.isValid(null));
    }

    @Test
    @DisplayName("isValid возвращает false для невалидного email")
    void isValidForInvalidEmail() {
        assertFalse(Email.isValid("not-an-email"));
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание с null бросает ошибку")
    void creationWithNullThrows() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(null));

        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
    }

    @Test
    @DisplayName("Создание с пустой строкой бросает ошибку")
    void creationWithBlankThrows() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("   "));

        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
    }

    @Test
    @DisplayName("Создание с невалидным форматом бросает ошибку")
    void creationWithInvalidFormatThrows() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("not-an-email"));

        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals(Map.of("email", "not-an-email"), ex.getDetails());
    }
}