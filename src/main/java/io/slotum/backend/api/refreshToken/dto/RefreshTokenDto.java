package io.slotum.backend.api.refreshToken.dto;

import java.time.Instant;

/**
 * Данные refresh-токена
 * @param id идентификатор токена
 * @param userId идентификатор пользователя, которому принадлежит токен
 * @param tokenHash хэш токена
 * @param issuedAt время выпуска токена
 * @param expiresAt время истечения токена
 * @param revoked {@code true}, если токен аннулирован
 */
public record RefreshTokenDto(
        Long id,
        Long userId,
        String tokenHash,
        Instant issuedAt,
        Instant expiresAt,
        boolean revoked
) { }