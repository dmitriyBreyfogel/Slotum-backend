package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {

    @Test
    @DisplayName("Корректное создание валидного пароля (хэшируется и не равен raw)")
    void createsPasswordHashFromValidRaw() {
        Password password = fromRaw("P@ssw0rd!");
        assertNotNull(password.value());
        assertFalse(password.value().isBlank());
        assertNotEquals("P@ssw0rd!", password.value());
    }

    @Test
    @DisplayName("Проверка совпадения пароля проходит для корректного raw")
    void matchesReturnsTrueForCorrectRaw() {
        Password password = fromRaw("P@ssw0rd!");
        assertTrue(password.matches("P@ssw0rd!"));
    }

    @Test
    @DisplayName("Проверка совпадения пароля не проходит для некорректного raw")
    void matchesReturnsFalseForWrongRaw() {
        Password password = fromRaw("P@ssw0rd!");
        assertFalse(password.matches("P@ssw0rd?"));
    }

    @Test
    @DisplayName("matches возвращает false для null")
    void matchesReturnsFalseForNull() {
        Password password = fromRaw("P@ssw0rd!");
        assertFalse(password.matches(null));
    }

    @Test
    @DisplayName("matches возвращает false для пустой строки")
    void matchesReturnsFalseForEmptyString() {
        Password password = fromRaw("P@ssw0rd!");
        assertFalse(password.matches(""));
    }

    @Test
    @DisplayName("matches возвращает false для строки из пробелов")
    void matchesReturnsFalseForBlankString() {
        Password password = fromRaw("P@ssw0rd!");
        assertFalse(password.matches("   "));
    }

    @Test
    @DisplayName("Пустой raw пароль недопустим")
    void rejectsEmptyRawPassword() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw(""));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Null raw пароль недопустим")
    void rejectsNullRawPassword() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw(null));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Пробелы в raw пароле недопустимы")
    void rejectsRawPasswordWithSpace() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw("pass word"));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Перевод строки в raw пароле недопустим")
    void rejectsRawPasswordWithNewLine() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw("pass\nword"));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Табуляция в raw пароле недопустима")
    void rejectsRawPasswordWithTab() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw("pass\tword"));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Русские символы в raw пароле недопустимы")
    void rejectsRawPasswordWithNonAscii() {
        AppException ex = assertThrows(AppException.class, () -> fromRaw("пароль"));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Граничные ASCII символы '!' и '~' допустимы")
    void allowsAsciiRangeBoundaries() {
        assertDoesNotThrow(() -> fromRaw("!"));
        assertDoesNotThrow(() -> fromRaw("~"));
    }

    @Test
    @DisplayName("Статическая валидация принимает валидный raw пароль")
    void validateRawDoesNotThrowForValidPassword() {
        assertDoesNotThrow(() -> Password.validateRaw("A1!z"));
    }

    @Test
    @DisplayName("Статическая валидация отклоняет невалидный raw пароль")
    void validateRawThrowsForInvalidPassword() {
        AppException ex = assertThrows(AppException.class, () -> Password.validateRaw("A 1!z"));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("fromHash создаёт пароль из хэша (с trim) и matches работает")
    void fromHashTrimsAndMatches() {
        Password original = fromRaw("P@ssw0rd!");
        String hash = original.value();

        Password restored = Password.fromHash("  " + hash + "  ");
        assertEquals(hash, restored.value());
        assertTrue(restored.matches("P@ssw0rd!"));
    }

    @Test
    @DisplayName("fromHash отклоняет null")
    void fromHashRejectsNull() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromHash(null));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("fromHash отклоняет пустую строку")
    void fromHashRejectsEmptyString() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromHash(""));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("fromHash отклоняет строку из пробелов")
    void fromHashRejectsBlankString() {
        AppException ex = assertThrows(AppException.class, () -> Password.fromHash("   "));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }
}

