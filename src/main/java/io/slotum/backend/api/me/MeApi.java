package io.slotum.backend.api.me;

import io.slotum.backend.api.me.dto.CreateOrganizationRequest;
import io.slotum.backend.api.me.dto.CreateSpecialistRequest;
import io.slotum.backend.api.me.dto.OrganizationDto;
import io.slotum.backend.api.me.dto.OrganizationMemberDto;
import io.slotum.backend.api.me.dto.SpecialistDto;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * HTTP-контракт управления данными текущего пользователя.
 */
@RequestMapping("/api/v1/me")
public interface MeApi {

    /**
     * Создаёт специалиста для текущего пользователя.
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
     *     текущий пользователь не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code SPECIALIST_ALREADY_EXISTS} -
     *     специалист для текущего пользователя уже существует</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param request данные создаваемого специалиста
     * @return ответ с созданным специалистом
     */
    @PostMapping("/specialist")
    ResponseEntity<SpecialistDto> createSpecialistFromMe(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateSpecialistRequest request
    );

    /**
     * Создаёт организацию и добавляет в неё текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - организация успешно создана</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с одним из кодов {@code EMPTY_ORGANIZATION_NAME},
     *     {@code TOO_LONG_ORGANIZATION_NAME}, {@code TOO_LONG_ORGANIZATION_DESCRIPTION},
     *     {@code INVALID_ORGANIZATION_ID} или {@code INVALID_SPECIALIST_USER_ID} -
     *     переданы некорректные данные организации или членства</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист текущего пользователя не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code ORGANIZATION_ALREADY_EXISTS} -
     *     организация с указанным названием уже существует</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param request данные создаваемой организации
     * @return ответ с созданной организацией
     */
    @PostMapping("/organizations")
    ResponseEntity<OrganizationDto> createMyOrganization(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateOrganizationRequest request
    );

    /**
     * Возвращает организации текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организации специалиста успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист текущего пользователя не найден</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @return ответ со списком организаций специалиста, который может быть пустым
     */
    @GetMapping("/organizations")
    ResponseEntity<List<OrganizationDto>> getMyOrganizations(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    );

    /**
     * Удаляет текущего специалиста из организации.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалист успешно удалён из организации</li>
     *     <li>{@code 400 Bad Request} с одним из кодов {@code INVALID_ORGANIZATION_ID}
     *     или {@code INVALID_SPECIALIST_USER_ID} - передан некорректный идентификатор</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист текущего пользователя не найден</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_MEMBERSHIP_NOT_FOUND} -
     *     текущий специалист не состоит в организации</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param organizationId идентификатор организации
     * @return ответ с удалённым членством в организации
     */
    @DeleteMapping("/organizations/{organizationId}")
    ResponseEntity<OrganizationMemberDto> removeMeFromOrganization(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("organizationId") Long organizationId
    );
}
