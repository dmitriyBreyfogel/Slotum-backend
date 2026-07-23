package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PhoneTest {

    @Test
    @DisplayName("Корректное создание валидного телефона без '+'")
    void createsValidPhoneWithoutPlus() {
        String expected = "79991234567";
        String actual = Phone.of("79991234567").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Корректное создание валидного телефона с '+'")
    void createsValidPhoneWithPlus() {
        String expected = "+79991234567";
        String actual = Phone.of("+79991234567").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Пробелы по краям номера удаляются")
    void trimsSpacesAroundPhone() {
        String expected = "79991234567";
        String actual = Phone.of("  79991234567  ").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Пробелы внутри номера удаляются")
    void removesSpacesInsidePhone() {
        String expected = "79991234567";
        String actual = Phone.of("7 999 123 45 67").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Дефисы внутри номера удаляются")
    void removesHyphensInsidePhone() {
        String expected = "79991234567";
        String actual = Phone.of("7-999-123-45-67").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Пробелы и дефисы одновременно удаляются")
    void removesSpacesAndHyphensTogether() {
        String expected = "+79991234567";
        String actual = Phone.of("+7 999-123 45-67").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Минимальная длина номера (10 цифр) валидна")
    void allowsMinLength10Digits() {
        String expected = "1234567890";
        String actual = Phone.of("1234567890").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Максимальная длина номера (15 цифр) валидна")
    void allowsMaxLength15Digits() {
        String expected = "123456789012345";
        String actual = Phone.of("123456789012345").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Номер короче 10 цифр невалиден")
    void rejectsTooShortLessThan10Digits() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("123456789"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("123456789", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Номер длиннее 15 цифр невалиден")
    void rejectsTooLongMoreThan15Digits() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("1234567890123456"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("1234567890123456", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Номер, начинающийся с 0, невалиден")
    void rejectsStartingWithZero() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("0123456789"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("0123456789", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Номер с '+', начинающийся с 0, невалиден")
    void rejectsStartingWithPlusZero() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("+0123456789"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("+0123456789", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Буквы в номере недопустимы")
    void rejectsLetters() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("abcdef"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("abcdef", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Смешанные буквы и цифры в номере недопустимы")
    void rejectsLettersMixedWithDigits() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("+7999abc4567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("+7999abc4567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Скобки в номере недопустимы")
    void rejectsParentheses() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("+7(999)1234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("+7(999)1234567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Точки в номере недопустимы")
    void rejectsDots() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("7.999.123.45.67"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("7.999.123.45.67", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Слеши в номере недопустимы")
    void rejectsSlashes() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("7999/1234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("7999/1234567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Нижнее подчёркивание в номере недопустимо")
    void rejectsUnderscore() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("7_9991234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("7_9991234567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Два символа '+' в начале недопустимы")
    void rejectsMultiplePlusAtStart() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("++79991234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("++79991234567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Символ '+' в середине номера недопустим")
    void rejectsPlusInMiddle() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("7+9991234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("7+9991234567", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Символ '+' в конце номера недопустим")
    void rejectsPlusAtEnd() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("79991234567+"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("79991234567+", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Пустая строка невалидна")
    void rejectsEmptyString() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of(""));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Строка только из пробелов невалидна")
    void rejectsOnlySpaces() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("     "));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("     ", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Строка только из дефисов невалидна")
    void rejectsOnlyHyphens() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("---"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("---", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Только символ '+' невалиден")
    void rejectsPlusOnly() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("+"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("+", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Недостаточно цифр после удаления пробелов и дефисов")
    void rejectsTooShortAfterRemovingSeparators() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("7-999-123-45"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("7-999-123-45", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Табуляция внутри номера недопустима")
    void rejectsTabInsidePhone() {
        AppException ex = assertThrows(AppException.class, () -> Phone.of("+7\t9991234567"));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("+7\t9991234567", ex.getDetails().get("phone"));
    }
}
