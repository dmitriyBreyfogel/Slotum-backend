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
        String normalizedMessage = StringUtils.normalize(message);

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
        if (decidedAt != null && decidedAt.isBefore(createdAt)) {
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
