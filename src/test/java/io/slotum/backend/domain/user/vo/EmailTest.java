package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {

    @Test
    @DisplayName("Корректное создание валидного email")
    void test01() {
        String expected = "slotum@gmail.com";
        String actual = new Email("slotum@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Отсутствует домен верхнего уровня, но точка присутствует")
    void test02() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail."));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует домен верхнего уровня и точки нет")
    void test03() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует название почтового сервиса, но присутствует домен с точкой")
    void test04() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Отсутствует название почтового сервися и домен верхнего уровня")
    void test05() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится в конце почты")
    void test06() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotumgmail.com@"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail.com@", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится по соседству с точкой домена (справа от неё)")
    void test07() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotumgmail.@com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail.@com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится по соседству с точкой домена (слева от неё)")
    void test08() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotumgmail@.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotumgmail@.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Символ @ находится в начале почты")
    void test09() {
        AppException ex = assertThrows(AppException.class, () -> new Email("@slotumgmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("@slotumgmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка внутри имени)")
    void test10() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slo.tum@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slo.tum@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка слева от имени)")
    void test11() {
        AppException ex = assertThrows(AppException.class, () -> new Email(".slotum@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals(".slotum@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почты (точка справа от имени)")
    void test12() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum.@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum.@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (внутри имени)")
    void test13() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gma.il.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.il.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (точка слева от имени)")
    void test14() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@.gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@.gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Есть точка в имени почтового сервиса (точка справа от имени)")
    void test15() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail..com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail..com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Точка справа от почты")
    void test16() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail.com."));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com.", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют цифры в имени почты")
    void test17() {
        String expected = "slotum123@gmail.com";
        String actual = new Email("slotum123@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в имени почты")
    void test18() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum/@gmail.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum/@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют цифры в имени почтового сервиса")
    void test19() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail123.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail123.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в имени почтового сервиса")
    void test20() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail/.com"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют цифры в домене верхнего уровня")
    void test21() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail.com123"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com123", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Присутствуют недопустимые символы в домене верхнего уровня")
    void test22() {
        AppException ex = assertThrows(AppException.class, () -> new Email("slotum@gmail.com/"));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.com/", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Буквы верхнего регистра в имени почты приведутся к нижнему")
    void test23() {
        String expected = "slotum@gmail.com";
        String actual = new Email("SLOTum@gmail.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Буквы верхнего регистра в имени почтового сервиса приведутся к нижнему")
    void test24() {
        String expected = "slotum@gmail.com";
        String actual = new Email("slotum@gMAIL.com").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Буквы верхнего регистра в домене верхнего уровня приведутся к нижнему")
    void test25() {
        String expected = "slotum@gmail.com";
        String actual = new Email("slotum@gmail.cOm").value();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Комплексный тест на регистры букв в почте")
    void test26() {
        String expected = "slotum@gmail.com";
        String actual = new Email("SLOTUM@GMAIL.COM").value();
        assertEquals(expected, actual);
    }
}
