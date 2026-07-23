package io.slotum.backend.domain.slot;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public final class Slot {
    private final Long id;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final SlotStatus status;
    private final Long specialistUserId;
    private final Long customerId;
    private final Long organizationId;

    private Slot(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = status;
        this.specialistUserId = specialistUserId;
        this.customerId = customerId;
        this.organizationId = organizationId;
    }

    /**
     * Создание слота
     * @param startsAt время начала слота
     * @param endsAt время окончания слота
     * @param status статус слота
     * @param specialistUserId идентификатор пользователя специалиста, создавшего слот
     * @param customerId идентификатор пользователя, записавшегося на слот
     * @param organizationId идентификатор организации, которой принадлежит слот
     * @return созданный слот по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_STARTS_AT} - время начала слота равно {@code null}</li>
     *          <li>{@code INVALID_SLOT_ENDS_AT} - время окончания слота равно {@code null}</li>
     *          <li>{@code INVALID_SLOT_TIME_RANGE} - время окончания раньше времени начала слота</li>
     *          <li>{@code INVALID_SLOT_STATUS} - статус слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_SPECIALIST_ID} - идентификатор пользователя специалиста не положительный</li>
     *          <li>{@code INVALID_SLOT_CUSTOMER_ID} - идентификатор пользователя не положительный</li>
     *          <li>{@code INVALID_SLOT_ORGANIZATION_ID} - идентификатор организации не положительный</li>
     *      </ul>
     */
    public static Slot create(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {
        return restore(
                null,
                startsAt,
                endsAt,
                status,
                specialistUserId,
                customerId,
                organizationId
        );
    }

    /**
     * Создаёт объект слота с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что слот новый.
     * @param id идентификатор слота
     * @param startsAt начало назначенного слота
     * @param endsAt конец назначенного слота
     * @param status статус слота
     * @param specialistUserId идентификатор пользователя специалиста, создающего слот
     * @param customerId идентификатор пользователя, который записывается на данный слот
     * @param organizationId идентификатор организации, которой принадлежит данный слот
     * @return созданный объект слота по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_ID} - идентификатор слота не положительный</li>
     *          <li>{@code INVALID_SLOT_STARTS_AT} - время начала слота равно {@code null}</li>
     *          <li>{@code INVALID_SLOT_ENDS_AT} - время окончания слота равно {@code null}</li>
     *          <li>{@code INVALID_SLOT_TIME_RANGE} - время окончания раньше времени начала слота</li>
     *          <li>{@code INVALID_SLOT_STATUS} - статус слота {@code null}</li>
     *          <li>{@code INVALID_SLOT_SPECIALIST_ID} - идентификатор пользователя специалиста не положительный</li>
     *          <li>{@code INVALID_SLOT_CUSTOMER_ID} - идентификатор пользователя не положительный</li>
     *          <li>{@code INVALID_SLOT_ORGANIZATION_ID} - идентификатор организации не положительный</li>
     *      </ul>
     */
    public static Slot restore(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {
        validateId(id);
        validateStartsAt(startsAt);
        validateEndsAt(endsAt);
        validateTimeRange(startsAt, endsAt);
        validateStatus(status);
        validateSpecialistUserId(specialistUserId);
        validateCustomerId(customerId, status);
        validateOrganizationId(organizationId);

        return new Slot(
                id,
                startsAt,
                endsAt,
                status,
                specialistUserId,
                customerId,
                organizationId
        );
    }

    /**
     * Создаёт новый слот со статусом {@code BOOKED} на основе текущего.
     * Текущий объект остаётся неизменным.
     * @param customerId идентификатор пользователя, забронировавшего данный слот
     * @return забронированный слот
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SLOT_CUSTOMER_ID} - идентификатор пользователя не положительный</li>
     *      </ul>
     */
    public Slot book(Long customerId) {
        return restore(
                id,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                specialistUserId,
                customerId,
                organizationId
        );
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public Long getSpecialistUserId() {
        return specialistUserId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_ID,
                    "Invalid slot id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateStartsAt(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_STARTS_AT,
                    "Slot startsAt is null"
            );
        }
    }

    private static void validateEndsAt(LocalDateTime endsAt) {
        if (endsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_ENDS_AT,
                    "Slot endsAt is null"
            );
        }
    }

    private static void validateTimeRange(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (!endsAt.isAfter(startsAt)) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_TIME_RANGE,
                    "Slot endsAt must be after startsAt",
                    Map.of(
                            "startsAt", startsAt,
                            "endsAt", endsAt
                    )
            );
        }
    }

    private static void validateStatus(SlotStatus status) {
        if (status == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_STATUS,
                    "Slot status is null"
            );
        }
    }

    private static void validateSpecialistUserId(Long specialistUserId) {
        if (specialistUserId == null || specialistUserId <= 0) {
            Map<String, Object> details = (specialistUserId == null)
                    ? Map.of()
                    : Map.of("specialistUserId", specialistUserId);
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_SPECIALIST_ID,
                    "Invalid slot specialistUserId",
                    details
            );
        }
    }

    private static void validateCustomerId(Long customerId, SlotStatus status) {
        if (customerId != null && customerId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                    "Invalid slot customerId",
                    Map.of("customerId", customerId)
            );
        }

        if (status == SlotStatus.FREE && customerId != null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                    "Free slot must not have customerId",
                    Map.of("customerId", customerId)
            );
        }

        if (status == SlotStatus.BOOKED && customerId == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                    "Booked slot must have customerId"
            );
        }
    }

    private static void validateOrganizationId(Long organizationId) {
        if (organizationId == null || organizationId <= 0) {
            Map<String, Object> details = (organizationId == null)
                    ? Map.of()
                    : Map.of("organizationId", organizationId);
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_ORGANIZATION_ID,
                    "Invalid slot organizationId",
                    details
            );
        }
    }
}
