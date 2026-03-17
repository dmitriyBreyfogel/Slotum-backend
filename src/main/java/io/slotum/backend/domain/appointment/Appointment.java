package io.slotum.backend.domain.appointment;

import io.slotum.backend.domain.appointment.vo.AppointmentStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public final class Appointment {
    private final Long id;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final AppointmentStatus status;
    private final Long specialistUserId;
    private final Long customerId;
    private final Long organizationId;

    private Appointment(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
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

    public static Appointment create(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            String status,
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

    public static Appointment restore(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            String status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {
        validateId(id);
        LocalDateTime normalizedStartsAt = validateStartsAt(startsAt);
        LocalDateTime normalizedEndsAt = validateEndsAt(endsAt);
        validateTimeRange(normalizedStartsAt, normalizedEndsAt);

        return new Appointment(
                id,
                normalizedStartsAt,
                normalizedEndsAt,
                new AppointmentStatus(status),
                validateSpecialistUserId(specialistUserId),
                validateCustomerId(customerId),
                validateOrganizationId(organizationId)
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

    public AppointmentStatus getStatus() {
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
                    ErrorCode.INVALID_APPOINTMENT_ID,
                    "Invalid appointment id",
                    Map.of("id", id)
            );
        }
    }

    private static LocalDateTime validateStartsAt(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_STARTS_AT,
                    "Appointment startsAt is null"
            );
        }
        return startsAt;
    }

    private static LocalDateTime validateEndsAt(LocalDateTime endsAt) {
        if (endsAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_ENDS_AT,
                    "Appointment endsAt is null"
            );
        }
        return endsAt;
    }

    private static void validateTimeRange(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (!endsAt.isAfter(startsAt)) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_TIME_RANGE,
                    "Appointment endsAt must be after startsAt",
                    Map.of("startsAt", startsAt, "endsAt", endsAt)
            );
        }
    }

    private static Long validateSpecialistUserId(Long specialistUserId) {
        if (specialistUserId == null || specialistUserId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID,
                    "Invalid appointment specialistUserId",
                    Map.of("specialistUserId", specialistUserId)
            );
        }
        return specialistUserId;
    }

    private static Long validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID,
                    "Invalid appointment customerId",
                    Map.of("customerId", customerId)
            );
        }
        return customerId;
    }

    private static Long validateOrganizationId(Long organizationId) {
        if (organizationId == null || organizationId <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID,
                    "Invalid appointment organizationId",
                    Map.of("organizationId", organizationId)
            );
        }
        return organizationId;
    }
}