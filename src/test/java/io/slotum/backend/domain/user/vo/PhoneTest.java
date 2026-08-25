package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class PhoneTest {

    /* Создание валидного номера */
    @Test
    @DisplayName("Создание номера с плюсом")
    void creationWithPlus() {
        Phone phone = Phone.of("+79999999999");

        assertEquals("+79999999999", phone.value());
    }

    @Test
    @DisplayName("Создание номера без плюса")
    void creationWithoutPlus() {
        Phone phone = Phone.of("79999999999");

        assertEquals("79999999999", phone.value());
    }

    @Test
    @DisplayName("Создание номера с пробелами и дефисами")
    void creationWithSpacesAndDashes() {
        Phone phone = Phone.of("+7 999 999-99-99");

        assertEquals("+79999999999", phone.value());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация пробелов в номере")
    void spacesNormalization() {
        Phone phone = Phone.of("+7 999 999 99 99");

        assertEquals("+79999999999", phone.value());
    }

    @Test
    @DisplayName("Нормализация дефисов в номере")
    void dashesNormalization() {
        Phone phone = Phone.of("+7-999-999-99-99");

        assertEquals("+79999999999", phone.value());
    }

    /* Проверка валидности */
    @Test
    @DisplayName("isValid возвращает true для валидного номера")
    void isValidForValidPhone() {
        assertTrue(Phone.isValid("+79999999999"));
    }

    @Test
    @DisplayName("isValid возвращает false для null")
    void isValidForNull() {
        assertFalse(Phone.isValid(null));
    }

    @Test
    @DisplayName("isValid возвращает false для невалидного номера")
    void isValidForInvalidPhone() {
        assertFalse(Phone.isValid("123"));
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание с null бросает ошибку")
    void creationWithNullThrows() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of(null));

        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
    }

    @Test
    @DisplayName("Создание с пустой строкой бросает ошибку")
    void creationWithBlankThrows() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("   "));

        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
    }

    @Test
    @DisplayName("Создание с невалидным форматом бросает ошибку")
    void creationWithInvalidFormatThrows() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("123"));

        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals(Map.of("phone", "123"), ex.getDetails());
    }
}