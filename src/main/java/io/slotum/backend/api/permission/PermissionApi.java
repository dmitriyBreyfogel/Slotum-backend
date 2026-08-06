package io.slotum.backend.api.permission;

import io.slotum.backend.api.permission.dto.CreatePermissionRequest;
import io.slotum.backend.api.permission.dto.PermissionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
 * HTTP-контракт управления разрешениями.
 */
@Validated
@RequestMapping("/api/v1/permissions")
public interface PermissionApi {

    /**
     * Создаёт новое разрешение.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - разрешение успешно создано</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @param request данные создаваемого разрешения
     * @return ответ с созданным разрешением
     */
    @PostMapping
    ResponseEntity<PermissionDto> create(
            @Valid
            @RequestBody
            CreatePermissionRequest request
    );

    /**
     * Возвращает разрешение по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - разрешение найдено</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code PERMISSION_NOT_FOUND} -
     *     разрешение не найдено</li>
     * </ul>
     *
     * @param id идентификатор разрешения
     * @return ответ с найденным разрешением
     */
    @GetMapping("/{id}")
    ResponseEntity<PermissionDto> getPermission(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает разрешение по коду.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - разрешение найдено</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code PERMISSION_NOT_FOUND} -
     *     разрешение не найдено</li>
     * </ul>
     *
     * @param code код разрешения
     * @return ответ с найденным разрешением
     */
    @GetMapping("/by-code")
    ResponseEntity<PermissionDto> getByCode(
            @NotBlank(message = "Code is blank")
            @Size(max = 100, message = "Too long code")
            @RequestParam
            String code
    );

    /**
     * Возвращает все разрешения.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - разрешения успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком разрешений, который может быть пустым
     */
    @GetMapping
    ResponseEntity<List<PermissionDto>> getAllPermissions();

    /**
     * Удаляет разрешение по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - разрешение успешно удалено</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code PERMISSION_NOT_FOUND} -
     *     разрешение не найдено</li>
     * </ul>
     *
     * @param id идентификатор разрешения
     * @return ответ с удалённым разрешением
     */
    @DeleteMapping("/{id}")
    ResponseEntity<PermissionDto> deletePermission(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет все разрешения.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - разрешения успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAllPermissions();
}
