package io.slotum.backend.api.user;

import io.slotum.backend.api.user.dto.CreateUserRequest;
import io.slotum.backend.api.user.dto.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * HTTP-контракт управления пользователями.
 */
@Validated
@RequestMapping("/api/v1/users")
public interface UserApi {

    /**
     * Создаёт нового пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - пользователь успешно создан</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов
     *     {@code INVALID_USER_EMAIL}, {@code INVALID_USER_PASSWORD} или
     *     {@code INVALID_USER_PHONE} - переданы некорректные данные пользователя</li>
     *     <li>{@code 409 Conflict} с кодом {@code USER_EMAIL_ALREADY_EXISTS} -
     *     пользователь с указанной электронной почтой уже существует</li>
     * </ul>
     *
     * @param request данные создаваемого пользователя
     * @return ответ с созданным пользователем
     */
    @PostMapping
    ResponseEntity<UserDto> create(
            @Valid
            @RequestBody
            CreateUserRequest request
    );

    /**
     * Возвращает пользователя по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователь найден</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     пользователь не найден</li>
     * </ul>
     *
     * @param id идентификатор пользователя
     * @return ответ с найденным пользователем
     */
    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUser(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            long id
    );

    /**
     * Возвращает всех пользователей.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователи успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком пользователей, который может быть пустым
     */
    @GetMapping
    ResponseEntity<List<UserDto>> getAllUsers();

    /**
     * Возвращает пользователя по электронной почте.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователь найден</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 400 Bad Request} с кодом {@code INVALID_USER_EMAIL} -
     *     передана некорректная электронная почта</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     пользователь не найден</li>
     * </ul>
     *
     * @param email электронная почта пользователя
     * @return ответ с найденным пользователем
     */
    @GetMapping("/by-email")
    ResponseEntity<UserDto> getByEmail(
            @NotBlank(message = "Email is blank")
            @Email(message = "Invalid email format")
            @Size(max = 255, message = "Too long email")
            @RequestParam
            String email
    );

    /**
     * Удаляет пользователя по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователь успешно удалён</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     пользователь не найден</li>
     * </ul>
     *
     * @param id идентификатор пользователя
     * @return ответ с удалённым пользователем
     */
    @DeleteMapping("/{id}")
    ResponseEntity<UserDto> deleteUser(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет всех пользователей.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователи успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAllUsers();
}
