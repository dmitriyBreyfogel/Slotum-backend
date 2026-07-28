package io.slotum.backend.api.user.dto;

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
        String surname,
        String firstName,
        String secondName,
        String email,
        String password,
        String phone
) {
}
