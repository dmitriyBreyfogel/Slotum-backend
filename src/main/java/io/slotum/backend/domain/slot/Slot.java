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
        LocalDateTime normalizedStartsAt = validateStartsAt(startsAt);
        LocalDateTime normalizedEndsAt = validateEndsAt(endsAt);
        SlotStatus normalizedStatus = validateStatus(status);
        validateTimeRange(normalizedStartsAt, normalizedEndsAt);

        return new Slot(
                id,
                normalizedStartsAt,
                normalizedEndsAt,
                normalizedStatus,
                validateSpecialistUserId(specialistUserId),
                validateCustomerId(customerId, normalizedStatus),
                validateOrganizationId(organizationId)
        );
    }

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

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_ID,
                    "Invalid slot id",
                    Map.of("id", id)
            );
        }
    }

    private static LocalDateTime validateStartsAt(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_STARTS_AT,
                    "Slot startsAt is null"
            );
        }
        return startsAt;
    }

    private static LocalDateTime validateEndsAt(LocalDateTime endsAt) {
        if (endsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_ENDS_AT,
                    "Slot endsAt is null"
            );
        }
        return endsAt;
    }

    private static void validateTimeRange(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (!endsAt.isAfter(startsAt)) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_TIME_RANGE,
                    "Slot endsAt must be after startsAt",
                    Map.of("startsAt", startsAt, "endsAt", endsAt)
            );
        }
    }

    private static SlotStatus validateStatus(SlotStatus status) {
        if (status == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_STATUS,
                    "Slot status is null"
            );
        }
        return status;
    }

    private static Long validateSpecialistUserId(Long specialistUserId) {
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
        return specialistUserId;
    }

    private static Long validateCustomerId(Long customerId, SlotStatus status) {
        if (status == SlotStatus.FREE) {
            if (customerId != null) {
                throw AppException.build(
                        ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                        "Free slot must not have customerId",
                        Map.of("customerId", customerId)
                );
            }
            return null;
        }

        if (status == SlotStatus.BOOKED && customerId == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                    "Invalid slot customerId",
                    Map.of()
            );
        }

        if (customerId != null && customerId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_CUSTOMER_ID,
                    "Invalid slot customerId",
                    Map.of("customerId", customerId)
            );
        }

        return customerId;
    }

    private static Long validateOrganizationId(Long organizationId) {
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
        return organizationId;
    }
}
