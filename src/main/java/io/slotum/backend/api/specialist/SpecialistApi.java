package io.slotum.backend.api.specialist;

import io.slotum.backend.api.specialist.dto.CreateSpecialistRequest;
import io.slotum.backend.api.specialist.dto.OrganizationDto;
import io.slotum.backend.api.specialist.dto.SpecialistDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * HTTP-контракт управления специалистами.
 */
@Validated
@RequestMapping("/api/v1/specialists")
public interface SpecialistApi {

    /**
     * Создаёт специалиста для существующего пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - специалист успешно создан</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с одним из кодов
     *     {@code INVALID_SPECIALIST_USER_ID}, {@code TOO_LONG_SPECIALIST_DESCRIPTION}
     *     или {@code INVALID_SPECIALIST_GRADE} - переданы некорректные данные специалиста</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     пользователь не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code SPECIALIST_ALREADY_EXISTS} -
     *     специалист для указанного пользователя уже существует</li>
     * </ul>
     *
     * @param request данные создаваемого специалиста
     * @return ответ с созданным специалистом
     */
    @PostMapping
    ResponseEntity<SpecialistDto> createSpecialist(
            @Valid
            @RequestBody
            CreateSpecialistRequest request
    );

    /**
     * Возвращает специалиста по идентификатору пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалист найден</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист не найден</li>
     * </ul>
     *
     * @param id идентификатор пользователя специалиста
     * @return ответ с найденным специалистом
     */
    @GetMapping("/{id}")
    ResponseEntity<SpecialistDto> getSpecialist(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            long id
    );

    /**
     * Возвращает организации специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организации специалиста успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист не найден</li>
     * </ul>
     *
     * @param specialistUserId идентификатор пользователя специалиста
     * @return ответ со списком организаций специалиста, который может быть пустым
     */
    @GetMapping("/{specialistUserId}/organizations")
    ResponseEntity<List<OrganizationDto>> getSpecialistOrganizations(
            @NotNull(message = "SpecialistUserId is null")
            @Positive(message = "SpecialistUserId is not positive")
            @PathVariable("specialistUserId")
            Long specialistUserId
    );

    /**
     * Возвращает всех специалистов.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалисты успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком специалистов, который может быть пустым
     */
    @GetMapping
    ResponseEntity<List<SpecialistDto>> getSpecialists();

    /**
     * Удаляет специалиста по идентификатору пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалист успешно удалён</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист не найден</li>
     * </ul>
     *
     * @param id идентификатор пользователя специалиста
     * @return ответ с удалённым специалистом
     */
    @DeleteMapping("/{id}")
    ResponseEntity<SpecialistDto> deleteSpecialist(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет всех специалистов.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалисты успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAllSpecialists();
}
