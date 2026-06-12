package io.slotum.backend.domain.appointmentRequest;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public final class AppointmentRequest {
    private static final int MAX_MESSAGE_LENGTH = 1024;

    private final Long id;
    private final Long slotId;
    private final Long customerId;
    private final AppointmentRequestStatus status;
    private final String message;
    private final LocalDateTime createdAt;
    private final LocalDateTime decidedAt;

    private AppointmentRequest(
            Long id,
            Long slotId,
            Long customerId,
            AppointmentRequestStatus status,
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

    public static AppointmentRequest create(
            Long slotId,
            Long customerId,
            String message,
            LocalDateTime createdAt
    ) {
        return restore(
                null,
                slotId,
                customerId,
                AppointmentRequestStatus.PENDING,
                message,
                createdAt,
                null
        );
    }

    public static AppointmentRequest restore(
            Long id,
            Long slotId,
            Long customerId,
            AppointmentRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {
        validateId(id);
        AppointmentRequestStatus normalizedStatus = validateStatus(status);

        return new AppointmentRequest(
                id,
                validateSlotId(slotId),
                validateCustomerId(customerId),
                normalizedStatus,
                validateAndNormalizeMessage(message),
                validateCreatedAt(createdAt),
                validateDecidedAt(decidedAt, normalizedStatus)
        );
    }

    public AppointmentRequest accept(LocalDateTime decidedAt) {
        return decide(AppointmentRequestStatus.ACCEPTED, decidedAt);
    }

    public AppointmentRequest reject(LocalDateTime decidedAt) {
        return decide(AppointmentRequestStatus.REJECTED, decidedAt);
    }

    public AppointmentRequest cancel(LocalDateTime decidedAt) {
        return decide(AppointmentRequestStatus.CANCELLED, decidedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSlotId() {
        return slotId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public AppointmentRequestStatus getStatus() {
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

    private AppointmentRequest decide(AppointmentRequestStatus targetStatus, LocalDateTime decidedAt) {
        if (status != AppointmentRequestStatus.PENDING) {
            Map<String, Object> details = id == null
                    ? Map.of("status", status)
                    : Map.of("id", id, "status", status);
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_NOT_PENDING,
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

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_ID,
                    "Invalid appointment request id",
                    Map.of("id", id)
            );
        }
    }

    private static Long validateSlotId(Long slotId) {
        if (slotId == null || slotId <= 0) {
            Map<String, Object> details = slotId == null
                    ? Map.of()
                    : Map.of("slotId", slotId);
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_SLOT_ID,
                    "Invalid appointment request slotId",
                    details
            );
        }
        return slotId;
    }

    private static Long validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            Map<String, Object> details = customerId == null
                    ? Map.of()
                    : Map.of("customerId", customerId);
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_CUSTOMER_ID,
                    "Invalid appointment request customerId",
                    details
            );
        }
        return customerId;
    }

    private static AppointmentRequestStatus validateStatus(AppointmentRequestStatus status) {
        if (status == null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_STATUS,
                    "Slot request status is null"
            );
        }
        return status;
    }

    private static String validateAndNormalizeMessage(String message) {
        if (message == null) {
            return null;
        }

        String normalizedMessage = message.trim();
        if (normalizedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_MESSAGE,
                    "Too long appointment request message",
                    Map.of("messageLength", normalizedMessage.length())
            );
        }

        return normalizedMessage.isEmpty() ? null : normalizedMessage;
    }

    private static LocalDateTime validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_CREATED_AT,
                    "Slot request createdAt is null"
            );
        }
        return createdAt;
    }

    private static LocalDateTime validateDecidedAt(
            LocalDateTime decidedAt,
            AppointmentRequestStatus status
    ) {
        if (status == AppointmentRequestStatus.PENDING && decidedAt != null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_DECIDED_AT,
                    "Pending appointment request must not have decidedAt",
                    Map.of("decidedAt", decidedAt)
            );
        }

        if (status != AppointmentRequestStatus.PENDING && decidedAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_APPOINTMENT_REQUEST_DECIDED_AT,
                    "Finished appointment request must have decidedAt"
            );
        }

        return decidedAt;
    }
}
