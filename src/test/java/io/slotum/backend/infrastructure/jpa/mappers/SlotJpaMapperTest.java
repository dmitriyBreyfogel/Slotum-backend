package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SlotJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> SlotJpaMapper.toDomain(null));
        assertEquals("SlotJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                SlotJpaMapper.toJpa(null, new SpecialistJpa(1L, "d", 0.0), new UserJpa(1L, "s", "f", null, "a@b.cc", "h", "+79991234567"), new OrganizationJpa(1L, "o", "d", 0.0))
        );
        assertEquals("Slot source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит все поля и нормализует статус (trim)")
    void toDomainMapsAllFieldsAndNormalizesStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        SlotJpa jpa = new SlotJpa(
                1L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                specialist,
                customer,
                organization
        );

        Slot slot = SlotJpaMapper.toDomain(jpa);

        assertEquals(1L, slot.getId());
        assertEquals(startsAt, slot.getStartsAt());
        assertEquals(endsAt, slot.getEndsAt());
        assertEquals(SlotStatus.BOOKED, slot.getStatus());
        assertEquals(10L, slot.getSpecialistUserId());
        assertEquals(20L, slot.getCustomerId());
        assertEquals(30L, slot.getOrganizationId());
    }

    @Test
    @DisplayName("toJpa: маппит все поля и сохраняет ссылки на сущности")
    void toJpaMapsAllFieldsAndPreservesRefs() {
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

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        SlotJpa jpa = SlotJpaMapper.toJpa(slot, specialist, customer, organization);

        assertEquals(1L, jpa.getId());
        assertEquals(startsAt, jpa.getStartsAt());
        assertEquals(endsAt, jpa.getEndsAt());
        assertEquals(SlotStatus.FREE, jpa.getStatus());
        assertSame(specialist, jpa.getSpecialist());
        assertNull(jpa.getCustomer());
        assertSame(organization, jpa.getOrganization());
    }

    @Test
    @DisplayName("toDomain: maps free slot without customer")
    void toDomainMapsFreeSlotWithoutCustomer() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        SlotJpa jpa = new SlotJpa(
                1L,
                startsAt,
                endsAt,
                SlotStatus.FREE,
                new SpecialistJpa(10L, "Some", 4.5),
                null,
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        Slot slot = SlotJpaMapper.toDomain(jpa);

        assertEquals(1L, slot.getId());
        assertEquals(SlotStatus.FREE, slot.getStatus());
        assertNull(slot.getCustomerId());
    }

    @Test
    @DisplayName("Round-trip: Slot -> SlotJpa -> Slot сохраняет поля")
    void roundTripPreservesFields() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        Slot source = Slot.restore(
                5L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        SpecialistJpa specialist = new SpecialistJpa(10L, "Some", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Desc", 0.0);

        Slot mapped = SlotJpaMapper.toDomain(SlotJpaMapper.toJpa(source, specialist, customer, organization));

        assertEquals(5L, mapped.getId());
        assertEquals(startsAt, mapped.getStartsAt());
        assertEquals(endsAt, mapped.getEndsAt());
        assertEquals(SlotStatus.BOOKED, mapped.getStatus());
        assertEquals(10L, mapped.getSpecialistUserId());
        assertEquals(20L, mapped.getCustomerId());
        assertEquals(30L, mapped.getOrganizationId());
    }

    @Test
    @DisplayName("toDomain: status = null -> AppException INVALID_SLOT_STATUS")
    void toDomainRejectsNullStatus() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        SlotJpa jpa = new SlotJpa(
                1L,
                startsAt,
                endsAt,
                null,
                new SpecialistJpa(10L, "Some", 4.5),
                new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567"),
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        AppException ex = assertThrows(AppException.class, () -> SlotJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_SLOT_STATUS, ex.getCode());
        assertTrue(ex.getDetails().isEmpty());
    }

    @Test
    @DisplayName("toDomain: specialist.userId <= 0 -> AppException INVALID_SLOT_SPECIALIST_ID")
    void toDomainRejectsInvalidSpecialistId() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        SlotJpa jpa = new SlotJpa(
                1L,
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                new SpecialistJpa(0L, "Some", 4.5),
                new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567"),
                new OrganizationJpa(30L, "Org", "Desc", 0.0)
        );

        AppException ex = assertThrows(AppException.class, () -> SlotJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_SLOT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
    }
}

