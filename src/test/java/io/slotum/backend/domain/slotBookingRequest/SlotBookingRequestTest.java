package io.slotum.backend.domain.slotBookingRequest;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class SlotBookingRequestTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 8, 26, 10, 0);
    private static final LocalDateTime DECIDED_AT = LocalDateTime.of(2026, 8, 26, 11, 0);

    /* Создание */
    @Test
    @DisplayName("Создание заявки со статусом PENDING")
    void creation() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "Message", CREATED_AT);

        assertNull(request.getId());
        assertEquals(1L, request.getSlotId());
        assertEquals(2L, request.getCustomerId());
        assertEquals(SlotBookingRequestStatus.PENDING, request.getStatus());
        assertEquals("Message", request.getMessage());
        assertEquals(CREATED_AT, request.getCreatedAt());
        assertNull(request.getDecidedAt());
    }

    @Test
    @DisplayName("Создание заявки с явным идентификатором")
    void creationWithId() {
        SlotBookingRequest request = SlotBookingRequest.restore(
                10L, 1L, 2L, SlotBookingRequestStatus.ACCEPTED, "Message", CREATED_AT, DECIDED_AT
        );

        assertEquals(10L, request.getId());
        assertEquals(SlotBookingRequestStatus.ACCEPTED, request.getStatus());
        assertEquals(DECIDED_AT, request.getDecidedAt());
    }

    /* Нормализация */
    @Test
    @DisplayName("Нормализация сообщения")
    void messageNormalization() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "  Message  ", CREATED_AT);

        assertEquals("Message", request.getMessage());
    }

    @Test
    @DisplayName("Создание заявки с пустым сообщением")
    void creationWithBlankMessage() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "   ", CREATED_AT);

        assertNull(request.getMessage());
    }

    /* Принятие решения */
    @Test
    @DisplayName("Одобрение заявки")
    void accept() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "Message", CREATED_AT);
        SlotBookingRequest accepted = request.accept(DECIDED_AT);

        assertEquals(SlotBookingRequestStatus.ACCEPTED, accepted.getStatus());
        assertEquals(DECIDED_AT, accepted.getDecidedAt());
        assertEquals(SlotBookingRequestStatus.PENDING, request.getStatus());
    }

    @Test
    @DisplayName("Отклонение заявки")
    void reject() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "Message", CREATED_AT);
        SlotBookingRequest rejected = request.reject(DECIDED_AT);

        assertEquals(SlotBookingRequestStatus.REJECTED, rejected.getStatus());
    }

    @Test
    @DisplayName("Отмена заявки")
    void cancel() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "Message", CREATED_AT);
        SlotBookingRequest cancelled = request.cancel(DECIDED_AT);

        assertEquals(SlotBookingRequestStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    @DisplayName("Решение по не PENDING заявке бросает ошибку")
    void decideOnNonPendingThrows() {
        SlotBookingRequest request = SlotBookingRequest.create(1L, 2L, "Message", CREATED_AT);
        SlotBookingRequest accepted = request.accept(DECIDED_AT);

        AppException ex = assertThrows(AppException.class, () -> accepted.accept(DECIDED_AT));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING, ex.getCode());
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание PENDING заявки с decidedAt бросает ошибку")
    void creationWithPendingAndDecidedAtThrows() {
        AppException ex = assertThrows(AppException.class, () ->
                SlotBookingRequest.restore(null, 1L, 2L, SlotBookingRequestStatus.PENDING, "Message", CREATED_AT, DECIDED_AT)
        );

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT, ex.getCode());
    }

    @Test
    @DisplayName("Создание не PENDING заявки без decidedAt бросает ошибку")
    void creationWithNonPendingWithoutDecidedAtThrows() {
        AppException ex = assertThrows(AppException.class, () ->
                SlotBookingRequest.restore(null, 1L, 2L, SlotBookingRequestStatus.ACCEPTED, "Message", CREATED_AT, null)
        );

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_DECIDED_AT, ex.getCode());
    }

    @Test
    @DisplayName("Создание заявки с decidedAt раньше createdAt бросает ошибку")
    void creationWithDecidedAtBeforeCreatedAtThrows() {
        AppException ex = assertThrows(AppException.class, () ->
                SlotBookingRequest.restore(null, 1L, 2L, SlotBookingRequestStatus.ACCEPTED, "Message", CREATED_AT, CREATED_AT.minusHours(1))
        );

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_TIME_RANGE, ex.getCode());
    }
}