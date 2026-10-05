package io.slotum.backend.domain.user;

import io.slotum.backend.domain.user.vo.Email;
import io.slotum.backend.domain.user.vo.Password;
import io.slotum.backend.domain.user.vo.Phone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class UserTest {

    /* Создание без отчества */
    @Test
    @DisplayName("Создание пользователя без отчества")
    void creationWithoutSecondName() {
        User user = User.create(
                "Breyfogel",
                "Dmitriy",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertNull(user.getId());
        assertEquals("Breyfogel", user.getSurname());
        assertEquals("Dmitriy", user.getFirstName());
        assertNull(user.getSecondName());
        assertEquals("dmitriy@example.com", user.getEmail().value());
        assertTrue(user.getPassword().matches("password123"));
        assertEquals("+79999999999", user.getPhone().value());
    }

    /* Создание с отчеством */
    @Test
    @DisplayName("Создание пользователя с отчеством")
    void creationWithSecondName() {
        User user = User.create(
                "Breyfogel",
                "Dmitriy",
                "Sergeevich",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertEquals("Sergeevich", user.getSecondName());
    }

    /* Создание с явным id */
    @Test
    @DisplayName("Создание пользователя с явным указанием идентификатора")
    void creationWithId() {
        User user = User.restore(
                1L,
                "Breyfogel",
                "Dmitriy",
                null,
                "dmitriy@example.com",
                Password.hash("password123"),
                "+79999999999"
        );

        assertEquals(1L, user.getId());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация фамилии")
    void surnameNormalization() {
        User user = User.create(
                "  Breyfogel  ",
                "Dmitriy",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertEquals("Breyfogel", user.getSurname());
    }

    @Test
    @DisplayName("Нормализация имени")
    void firstNameNormalization() {
        User user = User.create(
                "Breyfogel",
                "  Dmitriy  ",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertEquals("Dmitriy", user.getFirstName());
    }

    @Test
    @DisplayName("Нормализация отчества")
    void secondNameNormalization() {
        User user = User.create(
                "Breyfogel",
                "Dmitriy",
                "  Sergeevich  ",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertEquals("Sergeevich", user.getSecondName());
    }

    /* Проверка пароля */
    @Test
    @DisplayName("matchesPassword возвращает true для правильного пароля")
    void matchesPasswordReturnsTrue() {
        User user = User.create(
                "Breyfogel",
                "Dmitriy",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertTrue(user.matchesPassword("password123"));
    }

    @Test
    @DisplayName("matchesPassword возвращает false для неправильного пароля")
    void matchesPasswordReturnsFalse() {
        User user = User.create(
                "Breyfogel",
                "Dmitriy",
                "dmitriy@example.com",
                "password123",
                "+79999999999"
        );

        assertFalse(user.matchesPassword("wrongPassword"));
    }
}