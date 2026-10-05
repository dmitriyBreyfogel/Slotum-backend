package io.slotum.backend.api.auth.dto;

/**
 * Данные ответа при успешной аутентификации пользователя.
 *
 * @param accessToken токен доступа
 * @param tokenType тип токена
 */
public record LoginResponse(String accessToken, String tokenType) {
}
