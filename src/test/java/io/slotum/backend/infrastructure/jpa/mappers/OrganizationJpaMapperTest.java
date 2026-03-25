package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OrganizationJpaMapper.toDomain(null));
        assertEquals("OrganizationJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> OrganizationJpaMapper.toJpa(null));
        assertEquals("OrganizationJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит все поля и нормализует имя (trim)")
    void toDomainMapsAllFieldsAndNormalizesName() {
        OrganizationJpa jpa = new OrganizationJpa(
                7L,
                "  Org  ",
                "Some description",
                4.5
        );

        Organization organization = OrganizationJpaMapper.toDomain(jpa);

        assertEquals(7L, organization.getId());
        assertEquals("Org", organization.getName());
        assertEquals("Some description", organization.getDescription());
        assertEquals(4.5, organization.getGrade());
    }

    @Test
    @DisplayName("toJpa: маппит все поля как есть")
    void toJpaMapsAllFields() {
        Organization organization = Organization.restore(
                10L,
                "Org",
                "Desc",
                0.0
        );

        OrganizationJpa jpa = OrganizationJpaMapper.toJpa(organization);

        assertEquals(10L, jpa.getId());
        assertEquals("Org", jpa.getName());
        assertEquals("Desc", jpa.getDescription());
        assertEquals(0.0, jpa.getGrade());
    }

    @Test
    @DisplayName("Round-trip: Organization -> OrganizationJpa -> Organization сохраняет поля")
    void roundTripPreservesFields() {
        Organization source = Organization.restore(
                11L,
                "  Org  ",
                "  Desc  ",
                5.0
        );

        Organization mapped = OrganizationJpaMapper.toDomain(OrganizationJpaMapper.toJpa(source));

        assertEquals(11L, mapped.getId());
        assertEquals("Org", mapped.getName());
        assertEquals("  Desc  ", mapped.getDescription());
        assertEquals(5.0, mapped.getGrade());
    }

    @Test
    @DisplayName("toDomain: description = null -> AppException EMPTY_ORGANIZATION_DESCRIPTION")
    void toDomainRejectsNullDescription() {
        OrganizationJpa jpa = new OrganizationJpa(
                1L,
                "Org",
                null,
                4.5
        );

        AppException ex = assertThrows(AppException.class, () -> OrganizationJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION, ex.getCode());
    }

    @Test
    @DisplayName("toDomain: grade = null -> AppException INVALID_ORGANIZATION_GRADE")
    void toDomainRejectsNullGrade() {
        OrganizationJpa jpa = new OrganizationJpa(
                1L,
                "Org",
                "Desc",
                null
        );

        AppException ex = assertThrows(AppException.class, () -> OrganizationJpaMapper.toDomain(jpa));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_GRADE, ex.getCode());
    }
}

