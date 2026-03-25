package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> UserJpaMapper.toDomain(null));
        assertEquals("UserJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> UserJpaMapper.toJpa(null));
        assertEquals("User source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит все поля и применяет доменную нормализацию")
    void toDomainMapsAllFieldsAndNormalizes() {
        UserJpa jpa = new UserJpa(
                5L,
                "  Ivanov  ",
                "  Ivan  ",
                "   ",
                "  TeSt@Test.com  ",
                "RAW_PASS",
                " +7 999-123-45-67 "
        );

        User user = UserJpaMapper.toDomain(jpa);

        assertEquals(5L, user.getId());
        assertEquals("Ivanov", user.getSurname());
        assertEquals("Ivan", user.getFirstName());
        assertNull(user.getSecondName());
        assertEquals("test@test.com", user.getEmail().value());
        assertEquals("RAW_PASS", user.getPassword().value());
        assertEquals("+79991234567", user.getPhone().value());
    }

    @Test
    @DisplayName("toDomain: id может быть null")
    void toDomainAllowsNullId() {
        UserJpa jpa = new UserJpa(
                null,
                "Ivanov",
                "Ivan",
                null,
                "slotum@io.com",
                "HASH",
                "+79991234567"
        );

        User user = UserJpaMapper.toDomain(jpa);
        assertNull(user.getId());
    }

    @Test
    @DisplayName("toJpa: маппит все поля как есть (включая passwordHash)")
    void toJpaMapsAllFields() {
        User user = User.restore(
                10L,
                "Doe",
                "John",
                "Middle",
                "john@test.com",
                "HASH",
                "+79991234567"
        );

        UserJpa jpa = UserJpaMapper.toJpa(user);

        assertEquals(10L, jpa.getId());
        assertEquals("Doe", jpa.getSurname());
        assertEquals("John", jpa.getFirstName());
        assertEquals("Middle", jpa.getSecondName());
        assertEquals("john@test.com", jpa.getEmail());
        assertEquals("HASH", jpa.getPassword());
        assertEquals("+79991234567", jpa.getPhone());
    }

    @Test
    @DisplayName("Round-trip: User -> UserJpa -> User сохраняет пароль (matchesPassword)")
    void roundTripPreservesPasswordHash() {
        String rawPassword = "Password123!";
        User source = User.create(
                1L,
                "Doe",
                "John",
                null,
                "john@test.com",
                rawPassword,
                "+79991234567"
        );

        User mapped = UserJpaMapper.toDomain(UserJpaMapper.toJpa(source));

        assertEquals(source.getId(), mapped.getId());
        assertEquals(source.getEmail().value(), mapped.getEmail().value());
        assertTrue(mapped.matchesPassword(rawPassword));
    }
}

