package io.slotum.backend.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание пользователя.
 *
 * @param surname фамилия пользователя
 * @param firstName имя пользователя
 * @param secondName отчество пользователя, может быть {@code null}
 * @param email электронная почта пользователя
 * @param password пароль пользователя
 * @param phone номер телефона пользователя
 */
public record CreateUserRequest(
        @NotBlank(message = "Surname is blank")
        @Size(max = 255, message = "Too long surname")
        String surname,

        @NotBlank(message = "Firstname is blank")
        @Size(max = 255, message = "Too long firstname")
        String firstName,

        @Size(max = 255, message = "Too long secondname")
        String secondName,

        @NotBlank(message = "Email is blank")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Too long email")
        String email,

        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        String password,

        @NotBlank(message = "Phone is blank")
        @Size(max = 255, message = "Too long phone")
        String phone
) { }