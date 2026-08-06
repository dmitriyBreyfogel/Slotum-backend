package io.slotum.backend.api.auth;

import io.slotum.backend.api.auth.dto.LoginRequest;
import io.slotum.backend.api.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HTTP-контракт аутентификации пользователей.
 */
@Validated
@RequestMapping("/api/v1/auth")
public interface AuthApi {

    /**
     * Аутентифицирует пользователя по электронной почте и паролю.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - пользователь успешно аутентифицирован</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR}- тело запроса не прошло валидацию</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} - тело запроса содержит некорректный JSON</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code AUTH_INVALID_CREDENTIALS} -
     *     переданы неверные учётные данные</li>
     * </ul>
     *
     * @param request учётные данные пользователя
     * @return ответ с токеном доступа и типом токена
     */
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    );
}
