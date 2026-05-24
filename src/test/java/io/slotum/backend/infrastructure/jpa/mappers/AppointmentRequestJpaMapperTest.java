package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppointmentRequestJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> AppointmentRequestJpaMapper.toDomain(null)
        );

        assertEquals("AppointmentRequestJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> AppointmentRequestJpaMapper.toJpa(null, appointmentJpa(), userJpa())
        );

        assertEquals("AppointmentRequest source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: maps all fields")
    void toDomainMapsAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);
        AppointmentRequestJpa source = new AppointmentRequestJpa(
                1L,
                appointmentJpa(),
                userJpa(),
                AppointmentRequestStatus.ACCEPTED,
                "message",
                createdAt,
                decidedAt
        );

        AppointmentRequest result = AppointmentRequestJpaMapper.toDomain(source);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getAppointmentId());
        assertEquals(20L, result.getCustomerId());
        assertEquals(AppointmentRequestStatus.ACCEPTED, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(decidedAt, result.getDecidedAt());
    }

    @Test
    @DisplayName("toJpa: maps all fields and preserves refs")
    void toJpaMapsAllFieldsAndPreservesRefs() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        AppointmentRequest source = AppointmentRequest.restore(
                1L,
                5L,
                20L,
                AppointmentRequestStatus.PENDING,
                "message",
                createdAt,
                null
        );
        AppointmentJpa appointment = appointmentJpa();
        UserJpa customer = userJpa();

        AppointmentRequestJpa result = AppointmentRequestJpaMapper.toJpa(source, appointment, customer);

        assertEquals(1L, result.getId());
        assertSame(appointment, result.getAppointment());
        assertSame(customer, result.getCustomer());
        assertEquals(AppointmentRequestStatus.PENDING, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(createdAt, result.getCreatedAt());
        assertNull(result.getDecidedAt());
    }

    private static AppointmentJpa appointmentJpa() {
        return new AppointmentJpa(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                AppointmentStatus.FREE,
                new SpecialistJpa(10L, "description", 4.5),
                null,
                new OrganizationJpa(30L, "Org", "Description", 0.0)
        );
    }

    private static UserJpa userJpa() {
        return new UserJpa(
                20L,
                "Doe",
                "John",
                null,
                "john@test.com",
                "HASH",
                "+79991234567"
        );
    }
}
