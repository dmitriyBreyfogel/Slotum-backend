package io.slotum.backend.domain.user;

import io.slotum.backend.domain.user.vo.Password;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("Корректное создание пользователя с нормализацией полей")
    void createsUserAndNormalizesFields() {
        User user = User.create(
                null,
                "  Ivanov  ",
                "  Ivan ",
                "  Ivanovich  ",
                "  SLOTUM@GMAIL.COM  ",
                "P@ssw0rd!",
                " +7 999-123 45-67 "
        );

        assertNull(user.getId());
        assertEquals("Ivanov", user.getSurname());
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Ivanovich", user.getSecondName());
        assertEquals("slotum@gmail.com", user.getEmail().value());
        assertEquals("+79991234567", user.getPhone().value());

        assertNotEquals("P@ssw0rd!", user.getPassword().value());
        assertTrue(user.matchesPassword("P@ssw0rd!"));
    }

    @Test
    @DisplayName("ID может быть null при создании")
    void allowsNullId() {
        User user = User.create(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        );
        assertNull(user.getId());
    }

    @Test
    @DisplayName("ID может быть положительным при восстановлении")
    void allowsPositiveIdOnRestore() {
        String hash = new Password("P@ssw0rd!").value();
        User user = User.restore(
                1L,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                hash,
                "79991234567"
        );
        assertEquals(1L, user.getId());
    }

    @Test
    @DisplayName("ID равный 0 недопустим")
    void rejectsZeroId() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                0L,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Отрицательный ID недопустим")
    void rejectsNegativeId() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                -1L,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Фамилия обязательна (null недопустим)")
    void rejectsNullSurname() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                null,
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_SURNAME, ex.getCode());
        assertEquals("surname", ex.getDetails().get("field"));
        assertFalse(ex.getDetails().containsKey("value"));
    }

    @Test
    @DisplayName("Фамилия обязательна (пустая строка недопустима)")
    void rejectsBlankSurname() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "   ",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_SURNAME, ex.getCode());
        assertEquals("surname", ex.getDetails().get("field"));
        assertEquals("   ", ex.getDetails().get("value"));
    }

    @Test
    @DisplayName("Имя обязательно (null недопустим)")
    void rejectsNullFirstName() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "Ivanov",
                null,
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_FIRSTNAME, ex.getCode());
        assertEquals("firstName", ex.getDetails().get("field"));
        assertFalse(ex.getDetails().containsKey("value"));
    }

    @Test
    @DisplayName("Имя обязательно (пустая строка недопустима)")
    void rejectsBlankFirstName() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "Ivanov",
                "",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_FIRSTNAME, ex.getCode());
        assertEquals("firstName", ex.getDetails().get("field"));
        assertEquals("", ex.getDetails().get("value"));
    }

    @Test
    @DisplayName("Отчество необязательно (null сохраняется как null)")
    void keepsSecondNameNull() {
        User user = User.create(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        );
        assertNull(user.getSecondName());
    }

    @Test
    @DisplayName("Отчество необязательно (пустая строка нормализуется в null)")
    void normalizesBlankSecondNameToNull() {
        User user = User.create(
                null,
                "Ivanov",
                "Ivan",
                "   ",
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        );
        assertNull(user.getSecondName());
    }

    @Test
    @DisplayName("Отчество нормализуется (trim)")
    void trimsSecondName() {
        User user = User.create(
                null,
                "Ivanov",
                "Ivan",
                "  Ivanovich  ",
                "slotum@gmail.com",
                "P@ssw0rd!",
                "79991234567"
        );
        assertEquals("Ivanovich", user.getSecondName());
    }

    @Test
    @DisplayName("Невалидный email приводит к INVALID_USER_EMAIL")
    void rejectsInvalidEmail() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.",
                "P@ssw0rd!",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_EMAIL, ex.getCode());
        assertEquals("slotum@gmail.", ex.getDetails().get("email"));
    }

    @Test
    @DisplayName("Невалидный raw пароль приводит к INVALID_USER_PASSWORD")
    void rejectsInvalidRawPassword() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "pass word",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }

    @Test
    @DisplayName("Невалидный телефон приводит к INVALID_USER_PHONE")
    void rejectsInvalidPhone() {
        AppException ex = assertThrows(AppException.class, () -> User.create(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "P@ssw0rd!",
                "0123456789"
        ));
        assertEquals(ErrorCode.INVALID_USER_PHONE, ex.getCode());
        assertEquals("0123456789", ex.getDetails().get("phone"));
    }

    @Test
    @DisplayName("Восстановление пользователя из хэша пароля (fromHash) работает")
    void restoresUserFromPasswordHashAndMatches() {
        Password original = new Password("P@ssw0rd!");
        User user = User.restore(
                10L,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "  " + original.value() + "  ",
                "79991234567"
        );

        assertEquals(original.value(), user.getPassword().value());
        assertTrue(user.matchesPassword("P@ssw0rd!"));
    }

    @Test
    @DisplayName("Восстановление отклоняет пустой хэш пароля")
    void restoreRejectsBlankPasswordHash() {
        AppException ex = assertThrows(AppException.class, () -> User.restore(
                1L,
                "Ivanov",
                "Ivan",
                null,
                "slotum@gmail.com",
                "   ",
                "79991234567"
        ));
        assertEquals(ErrorCode.INVALID_USER_PASSWORD, ex.getCode());
    }
}
