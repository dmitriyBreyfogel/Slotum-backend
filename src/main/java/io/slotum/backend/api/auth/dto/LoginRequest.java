package io.slotum.backend.api.auth.dto;

/**
 * Данные запроса на аутентификацию пользователя.
 *
 * @param email электронная почта пользователя
 * @param password пароль пользователя
 */
public record LoginRequest(String email, String password) {
}
