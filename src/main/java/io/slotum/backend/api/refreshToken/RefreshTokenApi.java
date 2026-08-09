package io.slotum.backend.api.refreshToken;

import io.slotum.backend.api.refreshToken.dto.RefreshTokenDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * HTTP-контракт управления refresh-токенами
 */
@Validated
@RequestMapping("/api/v1/refreshTokens")
public interface RefreshTokenApi {

    /**
     * Возвращает refresh-токен по его идентификатору
     * <p>
     *      Возможные результаты выполнения запроса:
     *      <ul>
     *          <li>{@code 200 OK} - refresh-токен найден</li>
     *          <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *          данные запроса не прошли валидацию</li>
     *          <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *          аутентификация не выполнена</li>
     *          TODO <li>{@code 404 Not Found} с кодом {@code REFRESH_TOKEN_NOT_FOUND}</li>
     *      </ul>
     * </p>
     * @param id идентификатор refresh-токена
     * @return ответ с найденным refresh-токеном
     */
    @GetMapping("/{id}")
    ResponseEntity<RefreshTokenDto> getById(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable
            Long id
    );

    /**
     * Возвращает refresh-токен по его хэшу
     * <p>
     *      Возможные результаты выполнения запроса:
     *      <ul>
     *          <li>{@code OK} - refresh-токен найден</li>
     *          <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *          данные запроса не прошли валидацию</li>
     *          <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *          аутентификация не выполнена</li>
     *          TODO <li>{@code 404 Not Found} с кодом {@code REFRESH_TOKEN_NOT_FOUND}</li>
     *      </ul>
     * </p>
     * @param hash хэш токена
     * @return ответ с найденным refresh-токеном
     */
    @GetMapping("/by-hash/{hash}")
    ResponseEntity<RefreshTokenDto> getRefreshTokenByHash(
            @NotBlank(message = "Hash is blank")
            @Size(max = 255, message = "Too long hash")
            @PathVariable
            String hash
    );

    /**
     * Возвращает список всех refresh-токенов
     * <p>
     *      Возможные результаты выполнения запроса:
     *      <ul>
     *          <li>{@code 200 OK} - refresh-токены успешно получены</li>
     *          <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *          аутентификация не выполнена</li>
     *      </ul>
     * </p>
     * @return ответ со списком всех refresh-токенов, список может быть пустым
     */
    @GetMapping
    ResponseEntity<List<RefreshTokenDto>> getAllRefreshTokens();

    /**
     * Возвращает список всех refresh-токенов конкретного пользователя
     * <p>
     *     Возможные результаты выполнения запроса:
     *     <ul>
     *         <li>{@code 200 OK} - refresh-токены успешно получены</li>
     *         <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *         данные не прошли валидацию</li>
     *         <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *         аутентификация не выполнена</li>
     *     </ul>
     * </p>
     * @param userId идентификатор пользователя, токены которого получаем
     * @return ответ со списком всех refresh-токенов определённого пользователя,
     * список может быть пустым
     */
    @GetMapping("/by-user")
    ResponseEntity<List<RefreshTokenDto>> getAllRefreshTokensByUser(
            @NotNull(message = "UserId is null")
            @Positive(message = "UserId is not positive")
            @RequestParam
            Long userId
    );

    /**
     * Возвращает количество всех имеющихся refresh-токенов
     * <p>
     *     Возможные результаты выполнения запроса:
     *     <ul>
     *         <li>{@code 200 OK} - количество имеющихся refresh-токенов получено успешно</li>
     *         <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *         аутентификация не выполнена</li>
     *     </ul>
     * </p>
     * @return ответ с количеством всех имеющихся refresh-токенов, список может быть пустым
     */
    @GetMapping("/count")
    ResponseEntity<Long> countRefreshTokens();

    /**
     * Возвращает количество refresh-токенов в зависимости от переданного флага аннуляции.
     * <p>
     *     Возможные результаты выполнения запроса:
     *     <ul>
     *         <li>{@code 200 OK} - количество имеющихся refresh-токенов получено успешно</li>
     *         <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *         аутентификация не выполнена</li>
     *     </ul>
     * </p>
     * @param revoked флаг аннуляции. {@code true} - аннулированные, {@code false} - иначе
     * @return ответ с количеством всех имеющихся refresh-токенов, список может быть пустым
     */
    @GetMapping("/count/{revoked}")
    ResponseEntity<Long> countByRevokedRefreshTokens(
            @PathVariable
            boolean revoked
    );

    /**
     * Аннулирование refresh-токена по его хэшу
     * <p>
     *     Возможные результаты выполнения запроса:
     *     <ul>
     *         <li>{@code 200 OK} - успешное аннулирование refresh-токена</li>
     *         <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *         данные запроса не прошли валидацию</li>
     *         <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *         аутентификация не выполнена</li>
     *         TODO <li>{@code 404 Not Found} с кодом {@code REFRESH_TOKEN_NOT_FOUND}</li>
     *     </ul>
     * </p>
     * @param hash хэш токена
     * @return статус выполнения запроса
     */
    @PostMapping("/revoke/{hash}")
    ResponseEntity<Void> revokeRefreshTokenByHash(
            @NotBlank(message = "Hash is blank")
            @Size(max = 255, message = "Too long hash")
            @PathVariable
            String hash
    );

    /**
     * Аннулирование всех refresh-токенов определённого пользователя
     * <p>
     *     Возможные результаты выполнения запроса:
     *     <ul>
     *         <li>{@code 200 OK} - успешное аннулирование refresh-токенов пользователя</li>
     *         <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *         данные запроса не прошли валидацию</li>
     *         <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *         аутентификация не выполнена</li>
     *         TODO <li>{@code 404 Not Found} с кодом {@code REFRESH_TOKEN_NOT_FOUND}</li>
     *     </ul>
     * </p>
     * @param userId идентификатор пользователя, токены которого аннулируем
     * @return статус выполнения запроса
     */
    @PostMapping("/users/{userId}/revoke")
    ResponseEntity<Void> revokeRefreshTokensByUserId(
            @NotNull(message = "UserId is null")
            @Positive(message = "UserId is not positive")
            @PathVariable
            Long userId
    );

    /**
     * Удаляет истёкшие или аннулированные refresh-токены до указанного момента времени.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} — токены успешно удалены</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} —
     *         данные запроса не прошли валидацию</li>
     *     <li>{@code 400 Bad Request} с кодом {@code INVALID_REFRESH_TOKEN_TIME_RANGE} —
     *         переданный момент времени находится в будущем</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} —
     *         аутентификация не выполнена</li>
     * </ul>
     *
     * @param cutoff момент времени, до которого (строго раньше) удаляются токены
     * @return статус выполнения запроса
     */
    @DeleteMapping("/cleanup")
    ResponseEntity<Void> deleteExpiredOrRevokedBefore(
            @NotNull(message = "Cutoff is null")
            @RequestParam
            Instant cutoff
    );
}
