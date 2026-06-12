package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
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

public class SlotBookingRequestJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> SlotBookingRequestJpaMapper.toDomain(null)
        );

        assertEquals("SlotBookingRequestJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> SlotBookingRequestJpaMapper.toJpa(null, slotJpa(), userJpa())
        );

        assertEquals("SlotBookingRequest source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: maps all fields")
    void toDomainMapsAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        LocalDateTime decidedAt = LocalDateTime.of(2026, 5, 24, 11, 0);
        SlotBookingRequestJpa source = new SlotBookingRequestJpa(
                1L,
                slotJpa(),
                userJpa(),
                SlotBookingRequestStatus.ACCEPTED,
                "message",
                createdAt,
                decidedAt
        );

        SlotBookingRequest result = SlotBookingRequestJpaMapper.toDomain(source);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getSlotId());
        assertEquals(20L, result.getCustomerId());
        assertEquals(SlotBookingRequestStatus.ACCEPTED, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(decidedAt, result.getDecidedAt());
    }

    @Test
    @DisplayName("toJpa: maps all fields and preserves refs")
    void toJpaMapsAllFieldsAndPreservesRefs() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        SlotBookingRequest source = SlotBookingRequest.restore(
                1L,
                5L,
                20L,
                SlotBookingRequestStatus.PENDING,
                "message",
                createdAt,
                null
        );
        SlotJpa slot = slotJpa();
        UserJpa customer = userJpa();

        SlotBookingRequestJpa result = SlotBookingRequestJpaMapper.toJpa(source, slot, customer);

        assertEquals(1L, result.getId());
        assertSame(slot, result.getSlot());
        assertSame(customer, result.getCustomer());
        assertEquals(SlotBookingRequestStatus.PENDING, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(createdAt, result.getCreatedAt());
        assertNull(result.getDecidedAt());
    }

    private static SlotJpa slotJpa() {
        return new SlotJpa(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.FREE,
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
