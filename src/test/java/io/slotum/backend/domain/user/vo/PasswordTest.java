package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class PasswordTest {

    /* Создание из хэша */
    @Test
    @DisplayName("Создание пароля из хэша")
    void creationFromHash() {
        Password password = Password.fromHash("hash");

        assertEquals("hash", password.value());
    }

    @Test
    @DisplayName("Создание пароля из хэша с пробелами")
    void creationFromHashWithSpaces() {
        Password password = Password.fromHash("  hash  ");

        assertEquals("hash", password.value());
    }

    @Test
    @DisplayName("Создание пароля из null хэша бросает ошибку")
    void creationFromNullHashThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromHash(null));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Создание пароля из пустого хэша бросает ошибку")
    void creationFromBlankHashThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromHash("   "));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    /* Создание из сырого пароля */
    @Test
    @DisplayName("Создание пароля из сырого")
    void creationFromRaw() {
        Password password = Password.fromRaw("password123");

        assertNotEquals("password123", password.value());
        assertTrue(password.value().length() > 0);
    }

    @Test
    @DisplayName("Создание пароля из сырого с пробелами")
    void creationFromRawWithSpaces() {
        Password password = Password.fromRaw("  password123  ");

        assertEquals(password.value(), Password.fromRaw("password123").value());
    }

    @Test
    @DisplayName("Создание пароля из null сырого бросает ошибку")
    void creationFromNullRawThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromRaw(null));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Создание пароля из пустого сырого бросает ошибку")
    void creationFromBlankRawThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromRaw("   "));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    /* Сравнение паролей */
    @Test
    @DisplayName("matches возвращает true для одинаковых паролей")
    void matchesReturnsTrue() {
        Password password = Password.fromRaw("password123");

        assertTrue(password.matches("password123"));
    }

    @Test
    @DisplayName("matches возвращает false для разных паролей")
    void matchesReturnsFalse() {
        Password password = Password.fromRaw("password123");

        assertFalse(password.matches("wrongPassword"));
    }

    @Test
    @DisplayName("matches возвращает false для null")
    void matchesReturnsFalseForNull() {
        Password password = Password.fromRaw("password123");

        assertFalse(password.matches(null));
    }

    /* Валидация сырого пароля */
    @Test
    @DisplayName("validateRaw не бросает для валидного пароля")
    void validateRawForValidPassword() {
        Password.validateRaw("password123");
    }

    @Test
    @DisplayName("validateRaw бросает для null")
    void validateRawForNullThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.validateRaw(null));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("validateRaw бросает для пароля с пробелом")
    void validateRawForPasswordWithSpaceThrows() {
        AppException ex = assertThrows(AppException.class, () -> Password.validateRaw("pass word"));

        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }
}