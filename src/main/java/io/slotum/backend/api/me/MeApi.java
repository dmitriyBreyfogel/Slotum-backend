package io.slotum.backend.api.me;

import io.slotum.backend.api.me.dto.*;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequestMapping("/api/v1/me")
public interface MeApi {

    //------------------------SPECIALIST------------------------
    /**
     * Создаёт специалиста для текущего пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - специалист успешно создан</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} - тело запроса не прошло валидацию</li>
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
    @PreAuthorize("hasAuthority('CREATE_OWN_SPECIALIST')")
    @PostMapping("/specialist")
    ResponseEntity<SpecialistDto> createSpecialistFromMe(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @Valid
            @RequestBody
            CreateSpecialistRequest request
    );

    //------------------------ORGANIZATIONS------------------------
    /**
     * Создаёт организацию и добавляет в неё текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - организация успешно создана</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
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
    @PreAuthorize("hasAuthority('CREATE_OWN_ORGANIZATION')")
    @PostMapping("/organizations")
    ResponseEntity<OrganizationDto> createMyOrganization(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @Valid
            @RequestBody
            CreateOrganizationRequest request
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
    @PreAuthorize("hasAuthority('VIEW_OWN_ORGANIZATIONS')")
    @GetMapping("/organizations")
    ResponseEntity<List<OrganizationDto>> getMyOrganizations(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    /**
     * Удаляет текущего специалиста из организации.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалист успешно удалён из организации</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} - данные запроса не прошли валидацию</li>
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
    @PreAuthorize("hasAuthority('MANAGE_OWN_ORGANIZATION')")
    @DeleteMapping("/organizations/{organizationId}")
    ResponseEntity<OrganizationMemberDto> removeMeFromOrganization(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "OrganizationId is null")
            @Positive(message = "OrganizationId is not positive")
            @PathVariable("organizationId")
            Long organizationId
    );

    //------------------------NOTIFICATIONS------------------------
    @GetMapping("/notifications")
    ResponseEntity<List<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    @GetMapping("/notifications/unread")
    ResponseEntity<List<NotificationDto>> getMyUnreadNotifications(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    @GetMapping("/notifications/unread/count")
    ResponseEntity<Long> countMyUnreadNotifications(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    @PostMapping("/notifications/{notificationId}/read")
    ResponseEntity<Void> readMyNotification(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "NotificationId is null")
            @Positive(message = "NotificationId is not positive")
            @PathVariable("notificationId")
            Long notificationId
    );

    @PostMapping("/notifications/read-all")
    ResponseEntity<Long> readAllMyNotifications(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );
}
