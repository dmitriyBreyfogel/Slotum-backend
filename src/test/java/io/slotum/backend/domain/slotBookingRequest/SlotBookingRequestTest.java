package io.slotum.backend.domain.slotBookingRequest;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SlotBookingRequestTest {

    @Test
    @DisplayName("create: creates pending request with null id and decidedAt")
    void createCreatesPendingRequest() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);

        SlotBookingRequest request = SlotBookingRequest.create(
                1L,
                2L,
                "  please book me  ",
                createdAt
        );

        assertNull(request.getId());
        assertEquals(1L, request.getSlotId());
        assertEquals(2L, request.getCustomerId());
        assertEquals(SlotBookingRequestStatus.PENDING, request.getStatus());
        assertEquals("please book me", request.getMessage());
        assertEquals(createdAt, request.getCreatedAt());
        assertNull(request.getDecidedAt());
    }

    @Test
    @DisplayName("create: blank message is normalized to null")
    void createNormalizesBlankMessageToNull() {
        SlotBookingRequest request = SlotBookingRequest.create(
                1L,
                2L,
                "  ",
                LocalDateTime.of(2026, 5, 24, 10, 0)
        );

        assertNull(request.getMessage());
    }

    @Test
    @DisplayName("accept: pending request becomes accepted with decidedAt")
    void acceptPendingRequest() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);
        SlotBookingRequest request = SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.PENDING,
                "message",
                createdAt,
                null
        );

        SlotBookingRequest accepted = request.accept(decidedAt);

        assertEquals(1L, accepted.getId());
        assertEquals(SlotBookingRequestStatus.ACCEPTED, accepted.getStatus());
        assertEquals(decidedAt, accepted.getDecidedAt());
    }

    @Test
    @DisplayName("reject: pending request becomes rejected with decidedAt")
    void rejectPendingRequest() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);
        SlotBookingRequest request = SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.PENDING,
                "message",
                createdAt,
                null
        );

        SlotBookingRequest rejected = request.reject(decidedAt);

        assertEquals(SlotBookingRequestStatus.REJECTED, rejected.getStatus());
        assertEquals(decidedAt, rejected.getDecidedAt());
    }

    @Test
    @DisplayName("cancel: pending request becomes cancelled with decidedAt")
    void cancelPendingRequest() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);
        SlotBookingRequest request = SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.PENDING,
                "message",
                createdAt,
                null
        );

        SlotBookingRequest cancelled = request.cancel(decidedAt);

        assertEquals(SlotBookingRequestStatus.CANCELLED, cancelled.getStatus());
        assertEquals(decidedAt, cancelled.getDecidedAt());
    }

    @Test
    @DisplayName("restore: id <= 0 is invalid")
    void rejectsInvalidId() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.restore(
                0L,
                2L,
                3L,
                SlotBookingRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("create: slotId is required")
    void rejectsNullSlotId() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.create(
                null,
                3L,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0)
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_SLOT_ID, ex.getCode());
    }

    @Test
    @DisplayName("create: customerId is required")
    void rejectsNullCustomerId() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.create(
                2L,
                null,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0)
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CUSTOMER_ID, ex.getCode());
    }

    @Test
    @DisplayName("restore: status is required")
    void rejectsNullStatus() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                null,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_STATUS, ex.getCode());
    }

    @Test
    @DisplayName("create: createdAt is required")
    void rejectsNullCreatedAt() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.create(
                2L,
                3L,
                "message",
                null
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_CREATED_AT, ex.getCode());
    }

    @Test
    @DisplayName("create: message length must be at most 1024")
    void rejectsTooLongMessage() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.create(
                2L,
                3L,
                "a".repeat(1025),
                LocalDateTime.of(2026, 5, 24, 10, 0)
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_MESSAGE, ex.getCode());
        assertEquals(1025, ex.getDetails().get("messageLength"));
    }

    @Test
    @DisplayName("restore: pending request must not have decidedAt")
    void rejectsPendingWithDecidedAt() {
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                decidedAt
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT, ex.getCode());
        assertEquals(decidedAt, ex.getDetails().get("decidedAt"));
    }

    @Test
    @DisplayName("restore: finished request must have decidedAt")
    void rejectsFinishedWithoutDecidedAt() {
        AppException ex = assertThrows(AppException.class, () -> SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.ACCEPTED,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT, ex.getCode());
    }

    @Test
    @DisplayName("accept: finished request cannot be changed")
    void rejectsChangingFinishedRequest() {
        SlotBookingRequest request = SlotBookingRequest.restore(
                1L,
                2L,
                3L,
                SlotBookingRequestStatus.REJECTED,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        );

        AppException ex = assertThrows(AppException.class, () -> request.accept(
                LocalDateTime.of(2026, 5, 24, 12, 0)
        ));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        assertEquals(SlotBookingRequestStatus.REJECTED, ex.getDetails().get("status"));
    }
}
