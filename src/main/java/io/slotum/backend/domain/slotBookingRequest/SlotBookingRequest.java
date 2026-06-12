package io.slotum.backend.domain.slotBookingRequest;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public final class SlotBookingRequest {
    private static final int MAX_MESSAGE_LENGTH = 1024;

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
        SlotBookingRequestStatus normalizedStatus = validateStatus(status);

        return new SlotBookingRequest(
                id,
                validateSlotId(slotId),
                validateCustomerId(customerId),
                normalizedStatus,
                validateAndNormalizeMessage(message),
                validateCreatedAt(createdAt),
                validateDecidedAt(decidedAt, normalizedStatus)
        );
    }

    public SlotBookingRequest accept(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.ACCEPTED, decidedAt);
    }

    public SlotBookingRequest reject(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.REJECTED, decidedAt);
    }

    public SlotBookingRequest cancel(LocalDateTime decidedAt) {
        return decide(SlotBookingRequestStatus.CANCELLED, decidedAt);
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

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_ID,
                    "Invalid slot booking request id",
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
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_SLOT_ID,
                    "Invalid slot booking request slotId",
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
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CUSTOMER_ID,
                    "Invalid slot booking request customerId",
                    details
            );
        }
        return customerId;
    }

    private static SlotBookingRequestStatus validateStatus(SlotBookingRequestStatus status) {
        if (status == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_STATUS,
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
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_MESSAGE,
                    "Too long slot booking request message",
                    Map.of("messageLength", normalizedMessage.length())
            );
        }

        return normalizedMessage.isEmpty() ? null : normalizedMessage;
    }

    private static LocalDateTime validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw AppException.build(
                    ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CREATED_AT,
                    "Slot request createdAt is null"
            );
        }
        return createdAt;
    }

    private static LocalDateTime validateDecidedAt(
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

        return decidedAt;
    }
}
