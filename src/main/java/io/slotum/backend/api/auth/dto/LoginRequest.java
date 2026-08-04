package io.slotum.backend.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на аутентификацию пользователя.
 *
 * @param email электронная почта пользователя
 * @param password пароль пользователя
 */
public record LoginRequest(
        @NotBlank(message = "Email is blank")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Too long email")
        String email,

        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        String password
) { }