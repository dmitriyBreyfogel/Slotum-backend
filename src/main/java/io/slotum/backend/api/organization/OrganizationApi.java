package io.slotum.backend.api.organization;

import io.slotum.backend.api.organization.dto.CreateOrganizationRequest;
import io.slotum.backend.api.organization.dto.OrganizationDto;
import io.slotum.backend.api.organization.dto.OrganizationMemberDto;
import io.slotum.backend.api.organization.dto.SpecialistDto;
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
 * HTTP-контракт управления организациями и их специалистами.
 */
@Validated
@RequestMapping("/api/v1/organizations")
public interface OrganizationApi {

    /**
     * Создаёт новую организацию.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - организация успешно создана</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с одним из кодов {@code EMPTY_ORGANIZATION_NAME},
     *     {@code TOO_LONG_ORGANIZATION_NAME} или {@code TOO_LONG_ORGANIZATION_DESCRIPTION} -
     *     переданы некорректные данные организации</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 409 Conflict} с кодом {@code ORGANIZATION_ALREADY_EXISTS} -
     *     организация с указанным названием уже существует</li>
     * </ul>
     *
     * @param request данные создаваемой организации
     * @return ответ с созданной организацией
     */
    @PostMapping
    ResponseEntity<OrganizationDto> create(
            @Valid
            @RequestBody
            CreateOrganizationRequest request
    );

    /**
     * Добавляет специалиста в организацию.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - специалист успешно добавлен в организацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов {@code INVALID_ORGANIZATION_ID}
     *     или {@code INVALID_SPECIALIST_USER_ID} - передан некорректный идентификатор</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code ORGANIZATION_MEMBERSHIP_ALREADY_EXISTS} -
     *     специалист уже состоит в организации</li>
     * </ul>
     *
     * @param organizationId идентификатор организации
     * @param specialistUserId идентификатор пользователя специалиста
     * @return ответ с созданным членством в организации
     */
    @PostMapping("/{organizationId}/specialists/{specialistUserId}")
    ResponseEntity<OrganizationMemberDto> createOrganizationMember(
            @NotNull(message = "OrganizationId is null")
            @Positive(message = "OrganizationId is not positive")
            @PathVariable("organizationId")
            Long organizationId,

            @NotNull(message = "SpecialistUserId is null")
            @Positive(message = "SpecialistUserId is not positive")
            @PathVariable("specialistUserId")
            Long specialistUserId
    );

    /**
     * Возвращает организацию по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организация найдена</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     * </ul>
     *
     * @param id идентификатор организации
     * @return ответ с найденной организацией
     */
    @GetMapping("/{id}")
    ResponseEntity<OrganizationDto> getOrganization(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает все организации.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организации успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком организаций, который может быть пустым
     */
    @GetMapping
    ResponseEntity<List<OrganizationDto>> getAllOrganizations();

    /**
     * Возвращает специалистов организации.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - специалисты организации успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     * </ul>
     *
     * @param organizationId идентификатор организации
     * @return ответ со списком специалистов организации, который может быть пустым
     */
    @GetMapping("/{organizationId}/specialists")
    ResponseEntity<List<SpecialistDto>> getOrganizationSpecialists(
            @NotNull(message = "OrganizationId is null")
            @Positive(message = "OrganizationId is not positive")
            @PathVariable("organizationId")
            Long organizationId
    );

    /**
     * Удаляет организацию по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организация успешно удалена</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     * </ul>
     *
     * @param id идентификатор организации
     * @return ответ с удалённой организацией
     */
    @DeleteMapping("/{id}")
    ResponseEntity<OrganizationDto> deleteOrganization(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет специалиста из организации.
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
     *     специалист не найден</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_MEMBERSHIP_NOT_FOUND} -
     *     специалист не состоит в организации</li>
     * </ul>
     *
     * @param organizationId идентификатор организации
     * @param specialistUserId идентификатор пользователя специалиста
     * @return ответ с удалённым членством в организации
     */
    @DeleteMapping("/{organizationId}/specialists/{specialistUserId}")
    ResponseEntity<OrganizationMemberDto> deleteSpecialistFromOrganization(
            @NotNull(message = "OrganizationId is null")
            @Positive(message = "OrganizationId is not positive")
            @PathVariable("organizationId")
            Long organizationId,

            @NotNull(message = "SpecialistUserId is null")
            @Positive(message = "SpecialistUserId is not positive")
            @PathVariable("specialistUserId")
            Long specialistUserId
    );

    /**
     * Удаляет все организации.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - организации успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAllOrganizations();
}
