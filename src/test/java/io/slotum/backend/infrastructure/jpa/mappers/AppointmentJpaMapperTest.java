package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> AppointmentJpaMapper.toDomain(null));
        assertEquals("AppointmentJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                AppointmentJpaMapper.toJpa(null, new SpecialistJpa(1L, "d", 0.0), new UserJpa(1L, "s", "f", null, "a@b.cc", "h", "+79991234567"), new OrganizationJpa(1L, "o", "d", 0.0))
        );
        assertEquals("Appointment source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит все поля и нормализует статус (trim)")
    void toDomainMapsAllFieldsAndNormalizesStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        AppointmentJpa jpa = new AppointmentJpa(
                1L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                specialist,
                customer,
                organization
        );

        Appointment appointment = AppointmentJpaMapper.toDomain(jpa);

        assertEquals(1L, appointment.getId());
        assertEquals(startsAt, appointment.getStartsAt());
        assertEquals(endsAt, appointment.getEndsAt());
        assertEquals(AppointmentStatus.BOOKED, appointment.getStatus());
        assertEquals(10L, appointment.getSpecialistUserId());
        assertEquals(20L, appointment.getCustomerId());
        assertEquals(30L, appointment.getOrganizationId());
    }

    @Test
    @DisplayName("toJpa: маппит все поля и сохраняет ссылки на сущности")
    void toJpaMapsAllFieldsAndPreservesRefs() {
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

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        AppointmentJpa jpa = AppointmentJpaMapper.toJpa(appointment, specialist, customer, organization);

        assertEquals(1L, jpa.getId());
        assertEquals(startsAt, jpa.getStartsAt());
        assertEquals(endsAt, jpa.getEndsAt());
        assertEquals(AppointmentStatus.FREE, jpa.getStatus());
        assertSame(specialist, jpa.getSpecialist());
        assertNull(jpa.getCustomer());
        assertSame(organization, jpa.getOrganization());
    }

    @Test
    @DisplayName("toDomain: maps free appointment without customer")
    void toDomainMapsFreeAppointmentWithoutCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppointmentJpa jpa = new AppointmentJpa(
                1L,
                startsAt,
                endsAt,
                AppointmentStatus.FREE,
                new SpecialistJpa(10L, "Some", 4.5),
                null,
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        Appointment appointment = AppointmentJpaMapper.toDomain(jpa);

        assertEquals(1L, appointment.getId());
        assertEquals(AppointmentStatus.FREE, appointment.getStatus());
        assertNull(appointment.getCustomerId());
    }

    @Test
    @DisplayName("Round-trip: Appointment -> AppointmentJpa -> Appointment сохраняет поля")
    void roundTripPreservesFields() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Appointment source = Appointment.restore(
                5L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        Appointment mapped = AppointmentJpaMapper.toDomain(AppointmentJpaMapper.toJpa(source, specialist, customer, organization));

        assertEquals(5L, mapped.getId());
        assertEquals(startsAt, mapped.getStartsAt());
        assertEquals(endsAt, mapped.getEndsAt());
        assertEquals(AppointmentStatus.BOOKED, mapped.getStatus());
        assertEquals(10L, mapped.getSpecialistUserId());
        assertEquals(20L, mapped.getCustomerId());
        assertEquals(30L, mapped.getOrganizationId());
    }

    @Test
    @DisplayName("toDomain: status = null -> AppException INVALID_APPOINTMENT_STATUS")
    void toDomainRejectsNullStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppointmentJpa jpa = new AppointmentJpa(
                1L,
                startsAt,
                endsAt,
                null,
                new SpecialistJpa(10L, "Some", 4.5),
                new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567"),
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        AppException ex = assertThrows(AppException.class, () -> AppointmentJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("toDomain: specialist.userId <= 0 -> AppException INVALID_APPOINTMENT_SPECIALIST_ID")
    void toDomainRejectsInvalidSpecialistId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        AppointmentJpa jpa = new AppointmentJpa(
                1L,
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                new SpecialistJpa(0L, "Some", 4.5),
                new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567"),
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        AppException ex = assertThrows(AppException.class, () -> AppointmentJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
    }
}

