package io.slotum.backend.api.role;

import io.slotum.backend.api.role.dto.CreateRoleRequest;
import io.slotum.backend.api.role.dto.RoleDto;
import io.slotum.backend.domain.role.RoleNames;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * HTTP-контракт управления ролями.
 */
@Validated
@RequestMapping("/api/v1/roles")
public interface RoleApi {

    /**
     * Создаёт новую роль.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - роль успешно создана</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @param request данные создаваемой роли
     * @return ответ с созданной ролью
     */
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    @PostMapping
    ResponseEntity<RoleDto> create(
            @Valid
            @RequestBody
            CreateRoleRequest request
    );

    /**
     * Возвращает роль по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - роль найдена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ROLE_NOT_FOUND} -
     *     роль не найдена</li>
     * </ul>
     *
     * @param id идентификатор роли
     * @return ответ с найденной ролью
     */
    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    @GetMapping("/{id}")
    ResponseEntity<RoleDto> getRole(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает роль по названию.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - роль найдена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ROLE_NOT_FOUND} -
     *     роль не найдена</li>
     * </ul>
     *
     * @param name название роли
     * @return ответ с найденной ролью
     */
    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    @GetMapping("/by-name")
    ResponseEntity<RoleDto> getByName(
            @NotNull(message = "Name is null")
            @RequestParam
            RoleNames name
    );

    /**
     * Возвращает все роли.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - роли успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком ролей, который может быть пустым
     */
    @PreAuthorize("hasAuthority('VIEW_ROLES')")
    @GetMapping
    ResponseEntity<List<RoleDto>> getAllRoles();

    /**
     * Удаляет роль по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - роль успешно удалена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ROLE_NOT_FOUND} -
     *     роль не найдена</li>
     * </ul>
     *
     * @param id идентификатор роли
     * @return ответ с удалённой ролью
     */
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    @DeleteMapping("/{id}")
    ResponseEntity<RoleDto> deleteRole(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет все роли.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - роли успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    @DeleteMapping
    ResponseEntity<Void> deleteAllRoles();
}
