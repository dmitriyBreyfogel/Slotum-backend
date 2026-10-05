package io.slotum.backend.domain.refreshToken;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class RefreshTokenTest {

    private static final Instant NOW = Instant.now();

    /* Создание */
    @Test
    @DisplayName("Создание refresh токена")
    void creation() {
        RefreshToken token = RefreshToken.create(1L, "hash", NOW.plusSeconds(3600));

        assertNull(token.getId());
        assertEquals(1L, token.getUserId());
        assertEquals("hash", token.getTokenHash());
        assertNotNull(token.getIssuedAt());
        assertEquals(NOW.plusSeconds(3600), token.getExpiresAt());
        assertFalse(token.getRevoked());
    }

    @Test
    @DisplayName("Создание refresh токена с явным идентификатором")
    void creationWithId() {
        Instant issuedAt = NOW.minusSeconds(60);
        RefreshToken token = RefreshToken.restore(10L, 1L, "hash", issuedAt, NOW.plusSeconds(3600), false);

        assertEquals(10L, token.getId());
        assertEquals(1L, token.getUserId());
        assertEquals("hash", token.getTokenHash());
        assertEquals(issuedAt, token.getIssuedAt());
        assertEquals(NOW.plusSeconds(3600), token.getExpiresAt());
        assertFalse(token.getRevoked());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация хэша токена")
    void hashNormalization() {
        RefreshToken token = RefreshToken.create(1L, "  hash  ", NOW.plusSeconds(3600));

        assertEquals("hash", token.getTokenHash());
    }

    @Test
    @DisplayName("Создание токена с пустым хэшем бросает ошибку")
    void creationWithBlankHashThrows() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(1L, "   ", NOW.plusSeconds(3600))
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_HASH, ex.getCode());
    }

    /* Ревокация */
    @Test
    @DisplayName("Аннулирование токена")
    void revoke() {
        RefreshToken token = RefreshToken.create(1L, "hash", NOW.plusSeconds(3600));
        RefreshToken revoked = token.revoke();

        assertTrue(revoked.getRevoked());
        assertFalse(token.getRevoked());
    }

    /* Проверка валидности */
    @Test
    @DisplayName("Токен валиден, если не аннулирован и не истёк")
    void isValid() {
        RefreshToken token = RefreshToken.create(1L, "hash", NOW.plusSeconds(3600));

        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("Токен невалиден, если аннулирован")
    void isInvalidWhenRevoked() {
        RefreshToken token = RefreshToken.create(1L, "hash", NOW.plusSeconds(3600));
        RefreshToken revoked = token.revoke();

        assertFalse(revoked.isValid());
    }

    @Test
    @DisplayName("Токен невалиден, если истёк")
    void isInvalidWhenExpired() {
        Instant issuedAt = NOW.minusSeconds(7200);
        RefreshToken token = RefreshToken.restore(1L, 1L, "hash", issuedAt, NOW.minusSeconds(3600), false);

        assertFalse(token.isValid());
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание с null userId")
    void creationWithNullUserId() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(null, "hash", NOW.plusSeconds(3600))
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_USER_ID, ex.getCode());
    }

    @Test
    @DisplayName("Создание с неположительным userId")
    void creationWithNonPositiveUserId() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(0L, "hash", NOW.plusSeconds(3600))
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_USER_ID, ex.getCode());
        assertEquals(Map.of("userId", 0L), ex.getDetails());
    }

    @Test
    @DisplayName("Создание с null хэшем")
    void creationWithNullHash() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(1L, null, NOW.plusSeconds(3600))
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_HASH, ex.getCode());
    }

    @Test
    @DisplayName("Создание с null expiresAt")
    void creationWithNullExpiresAt() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(1L, "hash", null)
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_EXPIRES_AT, ex.getCode());
    }

    @Test
    @DisplayName("Создание с expiresAt раньше issuedAt")
    void creationWithExpiresAtBeforeIssuedAt() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.create(1L, "hash", NOW.minusSeconds(60))
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_TIME_RANGE, ex.getCode());
    }

    @Test
    @DisplayName("Создание с неположительным id при restore")
    void creationWithNonPositiveId() {
        AppException ex = assertThrows(AppException.class, () ->
                RefreshToken.restore(0L, 1L, "hash", NOW, NOW.plusSeconds(3600), false)
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN_ID, ex.getCode());
        assertEquals(Map.of("id", 0L), ex.getDetails());
    }
}