package io.slotum.backend.domain.appointment;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest {

    @Test
    @DisplayName("Корректное создание записи через create (id = null, статус нормализуется)")
    void createsAppointmentWithNormalizedStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment appointment = Appointment.create(
                startsAt,
                endsAt,
                "  CONFIRMED  ",
                10L,
                20L,
                30L
        );

        assertNull(appointment.getId());
        assertEquals(startsAt, appointment.getStartsAt());
        assertEquals(endsAt, appointment.getEndsAt());
        assertEquals("CONFIRMED", appointment.getStatus().value());
        assertEquals(10L, appointment.getSpecialistUserId());
        assertEquals(20L, appointment.getCustomerId());
        assertEquals(30L, appointment.getOrganizationId());
    }

    @Test
    @DisplayName("Корректное восстановление записи через restore с id")
    void restoresAppointmentWithId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment appointment = Appointment.restore(
                1L,
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        );

        assertEquals(1L, appointment.getId());
    }

    @Test
    @DisplayName("id равный 0 недопустим")
    void rejectsZeroId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.restore(
                0L,
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("Отрицательный id недопустим")
    void rejectsNegativeId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.restore(
                -1L,
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("startsAt null недопустим")
    void rejectsNullStartsAt() {
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                null,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STARTS_AT, ex.getCode());
    }

    @Test
    @DisplayName("endsAt null недопустим")
    void rejectsNullEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                null,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ENDS_AT, ex.getCode());
    }

    @Test
    @DisplayName("endsAt должен быть строго позже startsAt (равные даты недопустимы)")
    void rejectsEqualStartsAtAndEndsAt() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("endsAt должен быть строго позже startsAt (конец раньше начала недопустим)")
    void rejectsEndsBeforeStarts() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 11, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_TIME_RANGE, ex.getCode());
        assertEquals(startsAt, ex.getDetails().get("startsAt"));
        assertEquals(endsAt, ex.getDetails().get("endsAt"));
    }

    @Test
    @DisplayName("Пустой статус недопустим")
    void rejectsBlankStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "   ",
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS, ex.getCode());
        assertEquals("   ", ex.getDetails().get("status"));
    }

    @Test
    @DisplayName("Статус null недопустим")
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
    @DisplayName("Статус длиной 32 символа допустим")
    void allowsStatusLength32() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        String status = "a".repeat(32);
        Appointment appointment = Appointment.create(
                startsAt,
                endsAt,
                status,
                10L,
                20L,
                30L
        );

        assertEquals(status, appointment.getStatus().value());
    }

    @Test
    @DisplayName("Статус длиной 33 символа недопустим")
    void rejectsStatusLength33() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        String status = "a".repeat(33);
        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                status,
                10L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS, ex.getCode());
        assertEquals(status, ex.getDetails().get("status"));
    }

    @Test
    @DisplayName("specialistUserId null недопустим")
    void rejectsNullSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                null,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("specialistUserId <= 0 недопустим")
    void rejectsNonPositiveSpecialistUserId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                0L,
                20L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
    }

    @Test
    @DisplayName("customerId null недопустим")
    void rejectsNullCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                null,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("customerId <= 0 недопустим")
    void rejectsNonPositiveCustomerId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                0L,
                30L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("customerId"));
    }

    @Test
    @DisplayName("organizationId null недопустим")
    void rejectsNullOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                null
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("organizationId <= 0 недопустим")
    void rejectsNonPositiveOrganizationId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppException ex = assertThrows(AppException.class, () -> Appointment.create(
                startsAt,
                endsAt,
                "NEW",
                10L,
                20L,
                -1L
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("organizationId"));
    }
}

