package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialistJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> SpecialistJpaMapper.toDomain(null));
        assertEquals("SpecialistJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> SpecialistJpaMapper.toJpa(null));
        assertEquals("Specialist source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит все поля")
    void toDomainMapsAllFields() {
        SpecialistJpa jpa = new SpecialistJpa(
                10L,
                "Some description",
                4.5
        );

        Specialist specialist = SpecialistJpaMapper.toDomain(jpa);

        assertEquals(10L, specialist.getUserId());
        assertEquals("Some description", specialist.getDescription());
        assertEquals(4.5, specialist.getGrade());
    }

    @Test
    @DisplayName("toJpa: маппит все поля как есть")
    void toJpaMapsAllFields() {
        Specialist specialist = Specialist.create(
                10L,
                "Some description",
                4.5
        );

        SpecialistJpa jpa = SpecialistJpaMapper.toJpa(specialist);

        assertEquals(10L, jpa.getUserId());
        assertEquals("Some description", jpa.getDescription());
        assertEquals(4.5, jpa.getGrade());
    }

    @Test
    @DisplayName("Round-trip: Specialist -> SpecialistJpa -> Specialist сохраняет поля")
    void roundTripPreservesFields() {
        Specialist source = Specialist.create(
                10L,
                "   ",
                0.0
        );

        Specialist mapped = SpecialistJpaMapper.toDomain(SpecialistJpaMapper.toJpa(source));

        assertEquals(10L, mapped.getUserId());
        assertEquals("   ", mapped.getDescription());
        assertEquals(0.0, mapped.getGrade());
    }

    @Test
    @DisplayName("toDomain: userId <= 0 -> AppException INVALID_SPECIALIST_USER_ID")
    void toDomainRejectsInvalidUserId() {
        SpecialistJpa jpa = new SpecialistJpa(
                0L,
                "Some description",
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> SpecialistJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("id"));
    }

    @Test
    @DisplayName("toDomain: description = null -> AppException EMPTY_SPECIALIST_DESCRIPTION")
    void toDomainRejectsNullDescription() {
        SpecialistJpa jpa = new SpecialistJpa(
                10L,
                null,
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> SpecialistJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.EMPTY_SPECIALIST_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("toDomain: grade = null -> AppException INVALID_SPECIALIST_GRADE")
    void toDomainRejectsNullGrade() {
        SpecialistJpa jpa = new SpecialistJpa(
                10L,
                "Some description",
                null
        );

        AppException ex = assertThrows(AppException.class, () -> SpecialistJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_SPECIALIST_GRADE, ex.getCode());
    }
}

