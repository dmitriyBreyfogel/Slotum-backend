package io.slotum.backend.api.slotBookingRequest;

import io.slotum.backend.api.slotBookingRequest.dto.CreateSlotBookingRequest;
import io.slotum.backend.api.slotBookingRequest.dto.SlotBookingRequestDto;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * HTTP-контракт управления заявками на запись в слот.
 */
@Validated
@RequestMapping("/api/v1/slot-booking-requests")
public interface SlotBookingRequestApi {

    /**
     * Создаёт заявку текущего пользователя на запись в слот.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - заявка успешно создана</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     текущий пользователь не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_SLOT_NOT_FREE} -
     *     слот недоступен для записи</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_ALREADY_EXISTS} -
     *     текущий пользователь уже имеет активную заявку на этот слот</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param request данные создаваемой заявки
     * @return ответ с созданной заявкой
     */
    @PreAuthorize("hasAuthority('CREATE_BOOKING')")
    @PostMapping
    ResponseEntity<SlotBookingRequestDto> create(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @Valid
            @RequestBody
            CreateSlotBookingRequest request
    );

    /**
     * Возвращает заявку по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявка найдена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_BOOKING_REQUEST_NOT_FOUND} -
     *     заявка не найдена</li>
     * </ul>
     *
     * @param id идентификатор заявки
     * @return ответ с найденной заявкой
     */
    @PreAuthorize("hasAuthority('VIEW_ANY_BOOKINGS')")
    @GetMapping("/{id}")
    ResponseEntity<SlotBookingRequestDto> getById(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает заявки текущего пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявки успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     текущий пользователь не найден</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @return ответ со списком заявок пользователя, который может быть пустым
     */
    @PreAuthorize("hasAuthority('VIEW_OWN_BOOKINGS')")
    @GetMapping("/me")
    ResponseEntity<List<SlotBookingRequestDto>> getMy(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    /**
     * Возвращает входящие заявки на слоты текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - входящие заявки успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист текущего пользователя не найден</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @return ответ со списком входящих заявок, который может быть пустым
     */
    @PreAuthorize("hasAuthority('VIEW_ANY_BOOKINGS')")
    @GetMapping("/incoming")
    ResponseEntity<List<SlotBookingRequestDto>> getIncoming(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser
    );

    /**
     * Возвращает заявки на указанный слот текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявки на слот успешно получены</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 403 Forbidden} с кодом {@code SLOT_BOOKING_REQUEST_FORBIDDEN} -
     *     слот не принадлежит текущему специалисту</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param slotId идентификатор слота
     * @return ответ со списком заявок на слот, который может быть пустым
     */
    @PreAuthorize("hasAuthority('VIEW_ANY_BOOKINGS')")
    @GetMapping("/slots/{slotId}")
    ResponseEntity<List<SlotBookingRequestDto>> getBySlot(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "SlotId is null")
            @Positive(message = "SlotId is not positive")
            @PathVariable("slotId")
            Long slotId
    );

    /**
     * Принимает заявку на слот текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявка успешно принята</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запросы не прошли валидацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов
     *     {@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} или
     *     {@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} -
     *     состояние времени принятия решения некорректно</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 403 Forbidden} с кодом {@code SLOT_BOOKING_REQUEST_FORBIDDEN} -
     *     слот не принадлежит текущему специалисту</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_BOOKING_REQUEST_NOT_FOUND} -
     *     заявка не найдена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_NOT_PENDING} -
     *     заявка уже обработана</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_SLOT_NOT_FREE} -
     *     слот уже недоступен для записи</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param id идентификатор заявки
     * @return ответ с принятой заявкой
     */
    @PreAuthorize("hasAuthority('ACCEPT_BOOKING')")
    @PostMapping("/{id}/accept")
    ResponseEntity<SlotBookingRequestDto> accept(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Отклоняет заявку на слот текущего специалиста.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявка успешно отклонена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов
     *     {@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} или
     *     {@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} -
     *     состояние времени принятия решения некорректно</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 403 Forbidden} с кодом {@code SLOT_BOOKING_REQUEST_FORBIDDEN} -
     *     слот не принадлежит текущему специалисту</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_BOOKING_REQUEST_NOT_FOUND} -
     *     заявка не найдена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_NOT_PENDING} -
     *     заявка уже обработана</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param id идентификатор заявки
     * @return ответ с отклонённой заявкой
     */
    @PreAuthorize("hasAuthority('REJECT_BOOKING')")
    @PostMapping("/{id}/reject")
    ResponseEntity<SlotBookingRequestDto> reject(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Отменяет заявку текущего пользователя.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - заявка успешно отменена</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов
     *     {@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} или
     *     {@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} -
     *     состояние времени принятия решения некорректно</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 403 Forbidden} с кодом {@code SLOT_BOOKING_REQUEST_FORBIDDEN} -
     *     заявка принадлежит другому пользователю</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_BOOKING_REQUEST_NOT_FOUND} -
     *     заявка не найдена</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_BOOKING_REQUEST_NOT_PENDING} -
     *     заявка уже обработана</li>
     * </ul>
     *
     * @param currentUser текущий аутентифицированный пользователь
     * @param id идентификатор заявки
     * @return ответ с отменённой заявкой
     */
    @PreAuthorize("hasAuthority('CANCEL_OWN_BOOKING')")
    @PostMapping("/{id}/cancel")
    ResponseEntity<SlotBookingRequestDto> cancel(
            @AuthenticationPrincipal
            AuthenticatedUser currentUser,

            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );
}
