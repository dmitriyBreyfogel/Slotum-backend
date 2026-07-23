package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {

    @Test
    @DisplayName("Корректное создание валидного email")
    void createsValidEmail() {
        String expected = "slotum@gmail.com";
        String actual = Email.of(Of("slotum@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Отсутствует домен верхнего уровня, но точка присутствует")
    void rejectsMissingTopLevelDomainAfterDot() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotum@gmail."));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует домен верхнего уровня и точки нет")
    void rejectsMissingTopLevelDomainWithoutDot() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotum@gmail"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует название почтового сервиса, но присутствует домен с точкой")
    void rejectsMissingServiceNameButHasDomain() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotum@.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует название почтового сервися и домен верхнего уровня")
    void rejectsMissingDomain() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotum@"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится в конце почты")
    void rejectsAtAtEnd() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotumgmail.com@"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail.com@", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится по соседству с точкой домена (справа от неё)")
    void rejectsAtImmediatelyAfterDot() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotumgmail.@com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail.@com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится по соседству с точкой домена (слева от неё)")
    void rejectsAtImmediatelyBeforeDot() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("slotumgmail@.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail@.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится в начале почты")
    void rejectsAtAtStart() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(Of("@slotumgmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("@slotumgmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка внутри имени)")
    void rejectsDotInLocalPart() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slo.tum@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slo.tum@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка слева от имени)")
    void rejectsLeadingDotInLocalPart() {
        AppException ex = assertThrows(AppException.class, () -> Email.of(".slotum@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals(".slotum@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка справа от имени)")
    void rejectsTrailingDotInLocalPart() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum.@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum.@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (внутри имени)")
    void rejectsDotInServiceName() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gma.il.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gma.il.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (точка слева от имени)")
    void rejectsLeadingDotInServiceName() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@.gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@.gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (точка справа от имени)")
    void rejectsDoubleDotInDomain() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail..com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail..com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Точка справа от почты")
    void rejectsTrailingDotInEmail() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail.com."));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com.", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют цифры в имени почты")
    void allowsDigitsInLocalPart() {
        String expected = "slotum123@gmail.com";
        String actual = Email.of("slotum123@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в имени почты")
    void rejectsInvalidCharsInLocalPart() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum/@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum/@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствует нижнее подчёркивание в имени почты")
    void allowsUnderscoreInLocalPart() {
        String expected = "slo_tum@gmail.com";
        String actual = Email.of("slo_tum@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Присутствуют цифры в имени почтового сервиса")
    void rejectsDigitsInServiceName() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail123.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail123.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в имени почтового сервиса")
    void rejectsInvalidCharsInServiceName() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail/.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail/.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют цифры в домене верхнего уровня")
    void rejectsDigitsInTopLevelDomain() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail.com123"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com123", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в домене верхнего уровня")
    void rejectsInvalidCharsInTopLevelDomain() {
        AppException ex = assertThrows(AppException.class, () -> Email.of("slotum@gmail.com/"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com/", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Буквы верхнего регистра в имени почты приведутся к нижнему")
    void normalizesLocalPartToLowercase() {
        String expected = "slotum@gmail.com";
        String actual = Email.of("SLOTum@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Буквы верхнего регистра в имени почтового сервиса приведутся к нижнему")
    void normalizesServiceNameToLowercase() {
        String expected = "slotum@gmail.com";
        String actual = Email.of("slotum@gMAIL.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Буквы верхнего регистра в домене верхнего уровня приведутся к нижнему")
    void normalizesTopLevelDomainToLowercase() {
        String expected = "slotum@gmail.com";
        String actual = Email.of("slotum@gmail.cOm").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Комплексный тест на регистры букв в почте")
    void normalizesEmailToLowercase() {
        String expected = "slotum@gmail.com";
        String actual = Email.of("SLOTUM@GMAIL.COM").value();
        assertEquals(expected, actual);
    }
}
