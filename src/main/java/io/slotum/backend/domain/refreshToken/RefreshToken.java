package io.slotum.backend.domain.refreshToken;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.Instant;
import java.util.Map;

public final class RefreshToken {
    private final Long id;
    private final Long userId;
    private final String tokenHash;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final boolean revoked;

    private RefreshToken(Long id, Long userId, String tokenHash, Instant issuedAt, Instant expiresAt, boolean revoked) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }

    /**
     * Создание refresh токена
     * @param userId идентификатор пользователя, которому выдаётся данный токен
     * @param tokenHash хэш созданного токена
     * @param expiresAt время истечения токена
     * @return созданный объект refresh токена по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_REFRESH_TOKEN_USER_ID} - идентификатор пользователя равен {@code null} или неположителен</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_HASH} - хэш токена равен {@code null}</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_EXPIRES_AT} - время истечения токена равно {@code null}</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_TIME_RANGE} - время истечения токена раньше времени его издания</li>
     *      </ul>
     */
    public static RefreshToken create(Long userId, String tokenHash, Instant expiresAt) {
        return restore(null, userId, tokenHash, Instant.now(), expiresAt, false);
    }

    /**
     * Создаёт объект refresh токена с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что токен новый.
     * @param id идентификатор токена
     * @param userId идентификатор пользователя, которому выдаётся данный токен
     * @param tokenHash хэш созданного токена
     * @param issuedAt время издания токена
     * @param expiresAt время истечения токена
     * @param revoked флаг аннуляции токена
     * @return созданный объект refresh токена по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_REFRESH_TOKEN_ID} - идентификатор токена неположителен</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_USER_ID} - идентификатор пользователя равен {@code null} или неположителен</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_HASH} - хэш токена равен {@code null}</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_ISSUED_AT} - время издания токена равно {@code null} или позже времени истечения токена</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_EXPIRES_AT} - время истечения токена равно {@code null}</li>
     *          <li>{@code INVALID_REFRESH_TOKEN_TIME_RANGE} - время истечения токена раньше времени его издания</li>
     *      </ul>
     */
    public static RefreshToken restore(Long id, Long userId, String tokenHash, Instant issuedAt, Instant expiresAt, boolean revoked) {
        String normalizeTokenHash = StringUtils.normalize(tokenHash);

        validateId(id);
        validateUserId(userId);
        validateTokenHash(normalizeTokenHash);
        validateIssuedAt(issuedAt);
        validateExpiresAt(expiresAt);
        validateTimeRange(issuedAt, expiresAt);

        return new RefreshToken(id, userId, normalizeTokenHash, issuedAt, expiresAt, revoked);
    }

    /**
     * Аннулирует токен.
     * Токен, помеченный как аннулированный, не может быть использован для обновления.
     *
     * @return новый объект RefreshToken с флагом {@code revoked = true}
     */
    public RefreshToken revoke() {
        return restore(id, userId, tokenHash, issuedAt, expiresAt, true);
    }

    /**
     * Проверяет, действителен ли токен.
     * Токен действителен, если он не аннулирован и не истёк.
     *
     * @return {@code true}, если токен можно использовать для обновления
     */
    public boolean isValid() {
        return !revoked && Instant.now().isBefore(expiresAt);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public boolean getRevoked() {
        return revoked;
    }

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_ID,
                    "Invalid refresh token id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_USER_ID,
                    "Empty refresh token user id"
            );
        }

        if (userId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_USER_ID,
                    "Invalid refresh token user id",
                    Map.of("userId", userId)
            );
        }
    }

    private static void validateTokenHash(String tokenHash) {
        if (tokenHash == null) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_HASH,
                    "Refresh token is empty"
            );
        }
    }

    private static void validateIssuedAt(Instant issuedAt) {
        if (issuedAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_ISSUED_AT,
                    "Refresh token issued at is empty"
            );
        }

        if (issuedAt.isAfter(Instant.now())) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_ISSUED_AT,
                    "Refresh token issued at after now time",
                    Map.of("issuedAt", issuedAt)
            );
        }
    }

    private static void validateExpiresAt(Instant expiresAt) {
        if (expiresAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_EXPIRES_AT,
                    "Refresh token expired at is empty"
            );
        }
    }

    private static void validateTimeRange(Instant issuedAt, Instant expiresAt) {
        if (expiresAt.isBefore(issuedAt)) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_TIME_RANGE,
                    "Refresh token expires at before issued at",
                    Map.of(
                            "issuedAt", issuedAt,
                            "expiresAt", expiresAt
                    )
            );
        }
    }
}
