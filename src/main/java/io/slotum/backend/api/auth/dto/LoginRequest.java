package io.slotum.backend.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Данные запроса на аутентификацию пользователя.
 *
 * @param email электронная почта пользователя
 * @param password пароль пользователя
 */
public record LoginRequest(
        @NotBlank(message = "Email is blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is blank")
        String password
) { }