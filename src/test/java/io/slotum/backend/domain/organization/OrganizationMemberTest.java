package io.slotum.backend.domain.organization;

import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrganizationMemberTest {

    @Test
    @DisplayName("Создает связь организации и специалиста с валидными id")
    void createsOrganizationMember() {
        OrganizationMember organizationMember = OrganizationMember.create(1L, 10L);

        assertEquals(1L, organizationMember.getOrganizationId());
        assertEquals(10L, organizationMember.getSpecialistUserId());
    }

    @Test
    @DisplayName("organizationId = null недопустим")
    void rejectsNullOrganizationId() {
        AppException ex = assertThrows(AppException.class, () -> OrganizationMember.create(null, 10L));

        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, ex.getCode());
        assertEquals("organizationId", ex.getDetails().get("field"));
    }

    @Test
    @DisplayName("organizationId <= 0 недопустим")
    void rejectsNonPositiveOrganizationId() {
        AppException zero = assertThrows(AppException.class, () -> OrganizationMember.create(0L, 10L));
        AppException negative = assertThrows(AppException.class, () -> OrganizationMember.create(-1L, 10L));

        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, zero.getCode());
        assertEquals(0L, zero.getDetails().get("organizationId"));
        assertEquals(ErrorCode.INVALID_ORGANIZATION_ID, negative.getCode());
        assertEquals(-1L, negative.getDetails().get("organizationId"));
    }

    @Test
    @DisplayName("specialistUserId = null недопустим")
    void rejectsNullSpecialistUserId() {
        AppException ex = assertThrows(AppException.class, () -> OrganizationMember.create(1L, null));

        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, ex.getCode());
        assertEquals("specialistUserId", ex.getDetails().get("field"));
    }

    @Test
    @DisplayName("specialistUserId <= 0 недопустим")
    void rejectsNonPositiveSpecialistUserId() {
        AppException zero = assertThrows(AppException.class, () -> OrganizationMember.create(1L, 0L));
        AppException negative = assertThrows(AppException.class, () -> OrganizationMember.create(1L, -1L));

        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, zero.getCode());
        assertEquals(0L, zero.getDetails().get("specialistUserId"));
        assertEquals(ErrorCode.INVALID_SPECIALIST_USER_ID, negative.getCode());
        assertEquals(-1L, negative.getDetails().get("specialistUserId"));
    }
}
