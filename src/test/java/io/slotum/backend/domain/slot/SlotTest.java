package io.slotum.backend.domain.slot;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public final class SlotTest {

    private static final LocalDateTime STARTS_AT = LocalDateTime.of(2026, 8, 26, 10, 0);
    private static final LocalDateTime ENDS_AT = LocalDateTime.of(2026, 8, 26, 11, 0);

    /* Создание */
    @Test
    @DisplayName("Полное создание свободного слота")
    void fullCreation() {
        Slot slot = Slot.create(STARTS_AT, ENDS_AT, SlotStatus.FREE, 1L, null, 2L);

        assertNull(slot.getId());
        assertEquals(STARTS_AT, slot.getStartsAt());
        assertEquals(ENDS_AT, slot.getEndsAt());
        assertEquals(SlotStatus.FREE, slot.getStatus());
        assertEquals(1L, slot.getSpecialistUserId());
        assertNull(slot.getCustomerId());
        assertEquals(2L, slot.getOrganizationId());
    }

    @Test
    @DisplayName("Полное создание забронированного слота")
    void fullCreationBooked() {
        Slot slot = Slot.create(STARTS_AT, ENDS_AT, SlotStatus.BOOKED, 1L, 3L, 2L);

        assertEquals(SlotStatus.BOOKED, slot.getStatus());
        assertEquals(3L, slot.getCustomerId());
    }

    @Test
    @DisplayName("Создание слота с явным указанием идентификатора")
    void creationWithId() {
        Slot slot = Slot.restore(10L, STARTS_AT, ENDS_AT, SlotStatus.FREE, 1L, null, 2L);

        assertEquals(10L, slot.getId());
    }

    /* Бронирование */
    @Test
    @DisplayName("Бронирование свободного слота")
    void booking() {
        Slot slot = Slot.create(STARTS_AT, ENDS_AT, SlotStatus.FREE, 1L, null, 2L);
        Slot booked = slot.book(3L);

        assertEquals(SlotStatus.BOOKED, booked.getStatus());
        assertEquals(3L, booked.getCustomerId());
        assertEquals(SlotStatus.FREE, slot.getStatus());
        assertNull(slot.getCustomerId());
    }

    /* Невалидное создание */
    @Test
    @DisplayName("Создание слота с концом раньше начала")
    void creationWithEndsAtBeforeStartsAt() {
        AppException ex = assertThrows(AppException.class, () ->
                Slot.create(ENDS_AT, STARTS_AT, SlotStatus.FREE, 1L, null, 2L)
        );

        assertEquals(ErrorCode.INVALID_SLOT_TIME_RANGE, ex.getCode());
        assertEquals(Map.of("startsAt", ENDS_AT, "endsAt", STARTS_AT), ex.getDetails());
    }

    @Test
    @DisplayName("Создание слота с концом равным началу")
    void creationWithEndsAtEqualsStartsAt() {
        AppException ex = assertThrows(AppException.class, () ->
                Slot.create(STARTS_AT, STARTS_AT, SlotStatus.FREE, 1L, null, 2L)
        );

        assertEquals(ErrorCode.INVALID_SLOT_TIME_RANGE, ex.getCode());
    }

    @Test
    @DisplayName("Создание свободного слота с клиентом")
    void creationWithFreeSlotAndCustomer() {
        AppException ex = assertThrows(AppException.class, () ->
                Slot.create(STARTS_AT, ENDS_AT, SlotStatus.FREE, 1L, 3L, 2L)
        );

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        assertEquals(Map.of("customerId", 3L), ex.getDetails());
    }

    @Test
    @DisplayName("Создание забронированного слота без клиента")
    void creationWithBookedSlotWithoutCustomer() {
        AppException ex = assertThrows(AppException.class, () ->
                Slot.create(STARTS_AT, ENDS_AT, SlotStatus.BOOKED, 1L, null, 2L)
        );

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
    }
}