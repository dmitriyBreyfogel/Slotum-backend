package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.organization.OrganizationMember;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrganizationMemberJpaMapperTest {

    @Test
    @DisplayName("toDomain: source = null -> IllegalArgumentException")
    void toDomainRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> OrganizationMemberJpaMapper.toDomain(null)
        );

        assertEquals("OrganizationMemberJpa source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toJpa: source = null -> IllegalArgumentException")
    void toJpaRejectsNullSource() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> OrganizationMemberJpaMapper.toJpa(null)
        );

        assertEquals("OrganizationMember source is null", ex.getMessage());
    }

    @Test
    @DisplayName("toDomain: маппит composite id")
    void toDomainMapsCompositeId() {
        OrganizationMemberJpa source = new OrganizationMemberJpa(1L, 10L);

        OrganizationMember result = OrganizationMemberJpaMapper.toDomain(source);

        assertEquals(1L, result.getOrganizationId());
        assertEquals(10L, result.getSpecialistUserId());
    }

    @Test
    @DisplayName("toJpa: маппит composite id")
    void toJpaMapsCompositeId() {
        OrganizationMember source = OrganizationMember.create(1L, 10L);

        OrganizationMemberJpa result = OrganizationMemberJpaMapper.toJpa(source);

        assertEquals(1L, result.getId().getOrganizationId());
        assertEquals(10L, result.getId().getSpecialistId());
    }

    @Test
    @DisplayName("Round-trip: OrganizationMember -> OrganizationMemberJpa -> OrganizationMember сохраняет поля")
    void roundTripPreservesFields() {
        OrganizationMember source = OrganizationMember.create(1L, 10L);

        OrganizationMember result = OrganizationMemberJpaMapper.toDomain(OrganizationMemberJpaMapper.toJpa(source));

        assertEquals(1L, result.getOrganizationId());
        assertEquals(10L, result.getSpecialistUserId());
    }
}
