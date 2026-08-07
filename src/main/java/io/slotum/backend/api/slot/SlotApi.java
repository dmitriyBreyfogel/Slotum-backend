package io.slotum.backend.api.slot;

import io.slotum.backend.api.slot.dto.CreateSlotRequest;
import io.slotum.backend.api.slot.dto.SlotDto;
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
 * HTTP-контракт управления слотами для записи.
 */
@Validated
@RequestMapping("/api/v1/slots")
public interface SlotApi {

    /**
     * Создаёт новый слот для записи.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 201 Created} - слот успешно создан</li>
     *     <li>{@code 400 Bad Request} с кодом {@code BAD_REQUEST} -
     *     тело запроса содержит некорректный JSON</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     тело запроса не прошло валидацию</li>
     *     <li>{@code 400 Bad Request} с одним из кодов {@code INVALID_SLOT_TIME_RANGE},
     *     {@code INVALID_SLOT_CUSTOMER_ID} -
     *     переданы некорректные данные слота</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SPECIALIST_NOT_FOUND} -
     *     специалист не найден</li>
     *     <li>{@code 404 Not Found} с кодом {@code USER_NOT_FOUND} -
     *     пользователь, записанный в слот, не найден</li>
     *     <li>{@code 404 Not Found} с кодом {@code ORGANIZATION_NOT_FOUND} -
     *     организация не найдена</li>
     *     <li>{@code 409 Conflict} с кодом {@code SLOT_OVERLAPPING} -
     *     время слота пересекается с другим слотом специалиста</li>
     * </ul>
     *
     * @param request данные создаваемого слота
     * @return ответ с созданным слотом
     */
    @PostMapping
    ResponseEntity<SlotDto> create(
            @Valid
            @RequestBody
            CreateSlotRequest request
    );

    /**
     * Возвращает слот по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - слот найден</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     * </ul>
     *
     * @param id идентификатор слота
     * @return ответ с найденным слотом
     */
    @GetMapping("/{id}")
    ResponseEntity<SlotDto> getSlot(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Возвращает все слоты.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - слоты успешно получены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ со списком слотов, который может быть пустым
     */
    @GetMapping
    ResponseEntity<List<SlotDto>> getAllSlots();

    /**
     * Удаляет слот по идентификатору.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - слот успешно удалён</li>
     *     <li>{@code 400 Bad Request} с кодом {@code VALIDATION_ERROR} -
     *     данные запроса не прошли валидацию</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     *     <li>{@code 404 Not Found} с кодом {@code SLOT_NOT_FOUND} -
     *     слот не найден</li>
     * </ul>
     *
     * @param id идентификатор слота
     * @return ответ с удалённым слотом
     */
    @DeleteMapping("/{id}")
    ResponseEntity<SlotDto> deleteSlot(
            @NotNull(message = "Id is null")
            @Positive(message = "Id is not positive")
            @PathVariable("id")
            Long id
    );

    /**
     * Удаляет все слоты.
     *
     * <p>Возможные результаты выполнения запроса:
     * <ul>
     *     <li>{@code 200 OK} - слоты успешно удалены</li>
     *     <li>{@code 401 Unauthorized} с кодом {@code UNAUTHORIZED} -
     *     аутентификация не выполнена</li>
     * </ul>
     *
     * @return ответ без тела
     */
    @DeleteMapping
    ResponseEntity<Void> deleteAllSlots();
}
