package io.slotum.backend.domain.organizationMember;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class OrganizationMemberTest {

    @Test
    @DisplayName("Создание члена организации")
    void creation() {
        OrganizationMember member = OrganizationMember.create(1L, 2L);

        assertEquals(1L, member.getOrganizationId());
        assertEquals(2L, member.getSpecialistUserId());
    }

    @Test
    @DisplayName("Создание члена организации с нулевыми идентификаторами")
    void creationWithZeroIds() {
        OrganizationMember member = OrganizationMember.create(0L, 0L);

        assertEquals(0L, member.getOrganizationId());
        assertEquals(0L, member.getSpecialistUserId());
    }

    @Test
    @DisplayName("Создание члена организации с отрицательными идентификаторами")
    void creationWithNegativeIds() {
        OrganizationMember member = OrganizationMember.create(-1L, -2L);

        assertEquals(-1L, member.getOrganizationId());
        assertEquals(-2L, member.getSpecialistUserId());
    }

    @Test
    @DisplayName("Создание члена организации с null идентификаторами")
    void creationWithNullIds() {
        OrganizationMember member = OrganizationMember.create(null, null);

        assertNull(member.getOrganizationId());
        assertNull(member.getSpecialistUserId());
    }
}