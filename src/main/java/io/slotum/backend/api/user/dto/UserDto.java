package io.slotum.backend.api.user.dto;

/**
 * Данные пользователя.
 *
 * @param userId идентификатор пользователя
 * @param surname фамилия пользователя
 * @param firstName имя пользователя
 * @param secondName отчество пользователя, может быть {@code null}
 * @param email электронная почта пользователя
 * @param phone номер телефона пользователя
 */
public record UserDto(
        Long userId,
        String surname,
        String firstName,
        String secondName,
        String email,
        String phone
) {
}
