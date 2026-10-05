package io.slotum.backend.api.resource;

import io.slotum.backend.api.resource.dto.CreateResourceRequest;
import io.slotum.backend.api.resource.dto.ResourceDto;
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

import java.util.List;

/**
 * HTTP-контракт управления защищаемыми ресурсами.
 */
@Validated
@RequestMapping("/api/v1/resources")
public interface ResourceApi {

    /**
     * Создаёт новый защищаемый ресурс.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - ресурс успешно создан</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @param request данные создаваемого ресурса
     * @return ответ с созданным ресурсом
     */
    @PreAuthorize("hasAuthority('MANAGE_RESOURCES')")
    @PostMapping
    ResponseEntity<ResourceDto> create(
            @Valid
            @RequestBody
            CreateResourceRequest request
    );

    /**
     * Возвращает защищаемый ресурс по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - ресурс найден</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code RESOURCE_NOT_FOUND} -
     *     ресурс не найден</li>
     * </ul>
     *
     * @param id идентификатор ресурса
     * @return ответ с найденным ресурсом
     */
    @PreAuthorize("hasAuthority('VIEW_RESOURCES')")
    @GetMapping("/{id}")
    ResponseEntity<ResourceDto> getResource(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает все защищаемые ресурсы.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - ресурсы успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком ресурсов, который может быть пустым
     */
    @PreAuthorize("hasAuthority('VIEW_RESOURCES')")
    @GetMapping
    ResponseEntity<List<ResourceDto>> getAllResources();

    /**
     * Удаляет защищаемый ресурс по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - ресурс успешно удалён</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code RESOURCE_NOT_FOUND} -
     *     ресурс не найден</li>
     * </ul>
     *
     * @param id идентификатор ресурса
     * @return ответ с удалённым ресурсом
     */
    @PreAuthorize("hasAuthority('MANAGE_RESOURCES')")
    @DeleteMapping("/{id}")
    ResponseEntity<ResourceDto> deleteResource(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет все защищаемые ресурсы.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - ресурсы успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @PreAuthorize("hasAuthority('MANAGE_RESOURCES')")
    @DeleteMapping
    ResponseEntity<Void> deleteAllResources();
}
