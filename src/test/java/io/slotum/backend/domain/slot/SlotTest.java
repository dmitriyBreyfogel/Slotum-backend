package io.slotum.backend.domain.slot;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SlotTest {

    @Test
    @DisplayName("create: creates slot with null id")
    void createCreatesSlotWithNullId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Slot slot = Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        assertNull(slot.getId());
        assertEquals(startsAt, slot.getStartsAt());
        assertEquals(endsAt, slot.getEndsAt());
        assertEquals(SlotStatus.BOOKED, slot.getStatus());
        assertEquals(10L, slot.getSpecialistUserId());
        assertEquals(20L, slot.getCustomerId());
        assertEquals(30L, slot.getOrganizationId());
    }

    @Test
    @DisplayName("restore: restores slot with id")
    void restoreRestoresSlotWithId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Slot slot = Slot.restore(
                1L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        assertEquals(1L, slot.getId());
    }

    @Test
    @DisplayName("create: creates free slot without customer")
    void createCreatesFreeSlotWithoutCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Slot slot = Slot.create(
                startsAt,
                endsAt,
                SlotStatus.FREE,
                10L,
                null,
                30L
        );

        assertEquals(SlotStatus.FREE, slot.getStatus());
        assertNull(slot.getCustomerId());
    }

    @Test
    @DisplayName("book: returns booked slot with customer")
    void bookReturnsBookedSlotWithCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);
        Slot slot = Slot.restore(
                1L,
                startsAt,
                endsAt,
                SlotStatus.FREE,
                10L,
                null,
                30L
        );

        Slot booked = slot.book(20L);

        assertEquals(1L, booked.getId());
        assertEquals(SlotStatus.BOOKED, booked.getStatus());
        assertEquals(20L, booked.getCustomerId());
        assertEquals(10L, booked.getSpecialistUserId());
        assertEquals(30L, booked.getOrganizationId());
    }

    @Test
    @DisplayName("restore: id = 0 is invalid")
    void rejectsZeroId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.restore(
                0L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("restore: id < 0 is invalid")
    void rejectsNegativeId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.restore(
                -1L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("create: startsAt = null is invalid")
    void rejectsNullStartsAt() {
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                null,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_STARTS_AT, ex.getCode());
    }

    @Test
    @DisplayName("create: endsAt = null is invalid")
    void rejectsNullEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                null,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_ENDS_AT, ex.getCode());
    }

    @Test
    @DisplayName("create: endsAt must be after startsAt (equal is invalid)")
    void rejectsEqualStartsAtAndEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("create: endsAt must be after startsAt (end before start is invalid)")
    void rejectsEndsBeforeStarts() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 11, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("create: status = null is invalid")
    void rejectsNullStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                null,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_STATUS, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: specialistUserId = null is invalid")
    void rejectsNullSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                null,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_SPECIALIST_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: specialistUserId <= 0 is invalid")
    void rejectsNonPositiveSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                0L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
    }

    @Test
    @DisplayName("create: free slot with customerId is invalid")
    void rejectsCustomerIdForFreeSlot() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.FREE,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        assertEquals(20L, ex.getDetails().get("customerId"));
    }

    @Test
    @DisplayName("create: customerId = null is invalid")
    void rejectsNullCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                null,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: customerId <= 0 is invalid")
    void rejectsNonPositiveCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                0L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("customerId"));
    }

    @Test
    @DisplayName("create: organizationId = null is invalid")
    void rejectsNullOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                null
        ));

        assertEquals(ErrorCode.INVALID_SLOT_ORGANIZATION_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: organizationId <= 0 is invalid")
    void rejectsNonPositiveOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Slot.create(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                -1L
        ));

        assertEquals(ErrorCode.INVALID_SLOT_ORGANIZATION_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("organizationId"));
    }
}
