package io.slotum.backend.domain.slotBookingRequest;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public final class SlotBookingRequest {
    private final Long id;
    private final Long slotId;
    private final Long customerId;
    private final SlotBookingRequestStatus status;
    private final String message;
    private final LocalDateTime createdAt;
    private final LocalDateTime decidedAt;

    private SlotBookingRequest(
            Long id,
            Long slotId,
            Long customerId,
            SlotBookingRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {
        this.id = id;
        this.slotId = slotId;
        this.customerId = customerId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.decidedAt = decidedAt;
    }

    /**
     * Создание записи на слот
     * @param slotId идентификатор слота
     * @param customerId идентификатор пользователя, бронирующего слот
     * @param message сообщение (может быть {@code null})
     * @param createdAt время создания записи на слот
     * @return созданная запись на слот
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_SLOT_ID} - идентификатор слота не положительный</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_CUSTOMER_ID} - идентификатор пользователя, забронировавшего слот, не положительный</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_STATUS} - статус записи слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_MESSAGE} - сообщение записи слота более 1024 символов</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_CREATED_AT} - время создания записи слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} - время принятия решения по записи слота имеется на обрабатываемом слоте или наоборот</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} - время принятия решения записи слота раньше времени создания записи слота</li>
     *      </ul>
     */
    public static SlotBookingRequest create(
            Long slotId,
            Long customerId,
            String message,
            LocalDateTime createdAt
    ) {
        return restore(
                null,
                slotId,
                customerId,
                SlotBookingRequestStatus.PENDING,
                message,
                createdAt,
                null
        );
    }

    /**
     * Создаёт объект записи слота с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что запись слота новая.
     * @param id идентификатор записи слота
     * @param slotId идентификатор слота
     * @param customerId идентификатор пользователя, бронирующего слот
     * @param status статус записи на слот
     * @param message сообщение
     * @param createdAt время создания слота
     * @param decidedAt время принятия решения по записи слота
     * @return созданный объект записи слота
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_ID} - идентификатор записи слота не положительный</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_SLOT_ID} - идентификатор слота не положительный</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_CUSTOMER_ID} - идентификатор пользователя, забронировавшего слот, не положительный</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_STATUS} - статус записи слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_MESSAGE} - сообщение записи слота более 1024 символов</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_CREATED_AT} - время создания записи слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} - время принятия решения по записи слота имеется на обрабатываемом слоте или наоборот</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} - время принятия решения записи слота раньше времени создания записи слота</li>
     *      </ul>
     */
    public static SlotBookingRequest restore(
            Long id,
            Long slotId,
            Long customerId,
            SlotBookingRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {
        validateId(id);
        validateSlotId(slotId);
        validateCustomerId(customerId);
        validateStatus(status);

        String normalizedMessage = StringUtils.normalize(message);
        validateMessage(normalizedMessage);

        validateCreatedAt(createdAt);
        validateDecidedAt(decidedAt, status);
        validateTimeRange(createdAt, decidedAt);

        return new SlotBookingRequest(
                id,
                slotId,
                customerId,
                status,
                normalizedMessage,
                createdAt,
                decidedAt
        );
    }

    /**
     * Создание одобренной записи на слот
     * @param decidedAt время принятия решения об одобрении записи
     * @return одобренная запись на слот
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} - время принятия решения по записи слота имеется на обрабатываемом слоте или наоборот</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} - время принятия решения записи слота раньше времени создания записи слота</li>
     *          <li>{@code SLOT_BOOKING_REQUEST_NOT_PENDING} - данная запись на слот не находится на рассмотрении</li>
     *      </ul>
     */
    public SlotBookingRequest accept(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.ACCEPTED, decidedAt);
    }

    /**
     * Создание отменённой записи на слот
     * @param decidedAt время принятия решения об отмене записи
     * @return отменённая запись на слот
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} - время принятия решения по записи слота имеется на обрабатываемом слоте или наоборот</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} - время принятия решения записи слота раньше времени создания записи слота</li>
     *          <li>{@code SLOT_BOOKING_REQUEST_NOT_PENDING} - данная запись на слот не находится на рассмотрении</li>
     *      </ul>
     */
    public SlotBookingRequest reject(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.REJECTED, decidedAt);
    }

    /**
     * Создание отозванной записи на слот
     * @param decidedAt время принятия решения о возврате записи
     * @return отозванная запись на слот
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT} - время принятия решения по записи слота имеется на обрабатываемом слоте или наоборот</li>
     *          <li>{@code INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE} - время принятия решения записи слота раньше времени создания записи слота</li>
     *          <li>{@code SLOT_BOOKING_REQUEST_NOT_PENDING} - данная запись на слот не находится на рассмотрении</li>
     *      </ul>
     */
    public SlotBookingRequest cancel(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.CANCELLED, decidedAt);
    }

    private SlotBookingRequest decide(SlotBookingRequestStatus targetStatus, LocalDateTime decidedAt) {
        if (status != SlotBookingRequestStatus.PENDING) {
            Map<String, Object> details = id == null
                    ? Map.of("status", status)
                    : Map.of("id", id, "status", status);

            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING,
                    "Slot request is not pending",
                    details
            );
        }

        return restore(
                id,
                slotId,
                customerId,
                targetStatus,
                message,
                createdAt,
                decidedAt
        );
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public Long getSlotId() {
        return slotId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public SlotBookingRequestStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_ID,
                    "Invalid slot booking request id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateSlotId(Long slotId) {
        if (slotId == null || slotId <= 0) {
            Map<String, Object> details = slotId == null
                    ? Map.of()
                    : Map.of("slotId", slotId);

            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_SLOT_ID,
                    "Invalid slot booking request slotId",
                    details
            );
        }
    }

    private static void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            Map<String, Object> details = customerId == null
                    ? Map.of()
                    : Map.of("customerId", customerId);

            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CUSTOMER_ID,
                    "Invalid slot booking request customerId",
                    details
            );
        }
    }

    private static void validateStatus(SlotBookingRequestStatus status) {
        if (status == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_STATUS,
                    "Slot request status is null"
            );
        }
    }

    private static void validateMessage(String message) {
        if (message != null && message.length() > 1024) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_MESSAGE,
                    "Too long slot booking request message",
                    Map.of(
                            "Expected max length", 1024,
                            "Actual length", message.length()
                    )
            );
        }
    }

    private static void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CREATED_AT,
                    "Slot request createdAt is null"
            );
        }
    }

    private static void validateDecidedAt(
            LocalDateTime decidedAt,
            SlotBookingRequestStatus status
    ) {
        if (status == SlotBookingRequestStatus.PENDING && decidedAt != null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT,
                    "Pending slot booking request must not have decidedAt",
                    Map.of("decidedAt", decidedAt)
            );
        }

        if (status != SlotBookingRequestStatus.PENDING && decidedAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT,
                    "Finished slot booking request must have decidedAt"
            );
        }
    }

    private static void validateTimeRange(LocalDateTime createdAt, LocalDateTime decidedAt) {
        if (createdAt != null && decidedAt != null) {
            if (decidedAt.isBefore(createdAt)) {
                throw AppException.build(
                        ErrorCode.INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE,
                        "Invalid slot booking request time range. DecidedAt before createdAt",
                        Map.of(
                                "CreatedAt", createdAt.toString(),
                                "DecidedAt", decidedAt.toString()
                        )
                );
            }
        }
    }
}
