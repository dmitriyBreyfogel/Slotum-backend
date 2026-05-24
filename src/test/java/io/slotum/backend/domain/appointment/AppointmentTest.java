package io.slotum.backend.domain.appointment;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    @DisplayName("create: creates appointment with null id")
    void createCreatesAppointmentWithNullId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment appointment = Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        assertNull(appointment.getId());
        assertEquals(startsAt, appointment.getStartsAt());
        assertEquals(endsAt, appointment.getEndsAt());
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
        assertEquals(10L, appointment.getSpecialistUserId());
        assertEquals(20L, appointment.getCustomerId());
        assertEquals(30L, appointment.getOrganizationId());
    }

    @Test
    @DisplayName("restore: restores appointment with id")
    void restoreRestoresAppointmentWithId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment appointment = Appointment.restore(
                1L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        assertEquals(1L, appointment.getId());
    }

    @Test
    @DisplayName("create: creates free appointment without customer")
    void createCreatesFreeAppointmentWithoutCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment appointment = Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.FREE,
                10L,
                null,
                30L
        );

        assertEquals(AppointmentStatus.FREE, appointment.getStatus());
        assertNull(appointment.getCustomerId());
    }

    @Test
    @DisplayName("book: returns booked appointment with customer")
    void bookReturnsBookedAppointmentWithCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);
        Appointment appointment = Appointment.restore(
                1L,
                startsAt,
                endsAt,
                AppointmentStatus.FREE,
                10L,
                null,
                30L
        );

        Appointment booked = appointment.book(20L);

        assertEquals(1L, booked.getId());
        assertEquals(AppointmentStatus.BOOKED, booked.getStatus());
        assertEquals(20L, booked.getCustomerId());
        assertEquals(10L, booked.getSpecialistUserId());
        assertEquals(30L, booked.getOrganizationId());
    }

    @Test
    @DisplayName("restore: id = 0 is invalid")
    void rejectsZeroId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.restore(
                0L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("restore: id < 0 is invalid")
    void rejectsNegativeId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.restore(
                -1L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("create: startsAt = null is invalid")
    void rejectsNullStartsAt() {
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                null,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STARTS_AT, ex.getCode());
    }

    @Test
    @DisplayName("create: endsAt = null is invalid")
    void rejectsNullEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                null,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ENDS_AT, ex.getCode());
    }

    @Test
    @DisplayName("create: endsAt must be after startsAt (equal is invalid)")
    void rejectsEqualStartsAtAndEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("create: endsAt must be after startsAt (end before start is invalid)")
    void rejectsEndsBeforeStarts() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 11, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("create: status = null is invalid")
    void rejectsNullStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                null,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: specialistUserId = null is invalid")
    void rejectsNullSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                null,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: specialistUserId <= 0 is invalid")
    void rejectsNonPositiveSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                0L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
    }

    @Test
    @DisplayName("create: free appointment with customerId is invalid")
    void rejectsCustomerIdForFreeAppointment() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.FREE,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertEquals(20L, ex.getDetails().get("customerId"));
    }

    @Test
    @DisplayName("create: customerId = null is invalid")
    void rejectsNullCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                null,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: customerId <= 0 is invalid")
    void rejectsNonPositiveCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                0L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("customerId"));
    }

    @Test
    @DisplayName("create: organizationId = null is invalid")
    void rejectsNullOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                null
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("create: organizationId <= 0 is invalid")
    void rejectsNonPositiveOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                -1L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("organizationId"));
    }
}
