package io.slotum.backend.domain.organization;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class OrganizationMember {
    private final Long organizationId;
    private final Long specialistUserId;

    private OrganizationMember(final Long organizationId, final Long specialistUserId) {
        validateOrganizationId(organizationId);
        validateSpecialistUserId(specialistUserId);

        this.organizationId = organizationId;
        this.specialistUserId = specialistUserId;
    }

    public static OrganizationMember create(Long organizationId, Long specialistUserId) {
        return new OrganizationMember(organizationId, specialistUserId);
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public Long getSpecialistUserId() {
        return specialistUserId;
    }

    private static void validateOrganizationId(Long id) {
        if (!validateId(id)) {
            throw AppException.build(
                    ErrorCode.INVALID_ORGANIZATION_ID,
                    "Invalid organization id",
                    idDetails("organizationId", id)
            );
        }
    }

    private static void validateSpecialistUserId(Long id) {
        if (!validateId(id)) {
            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_USER_ID,
                    "Invalid specialist userId",
                    idDetails("specialistUserId", id)
            );
        }
    }

    private static boolean validateId(Long id) {
        return id != null && id > 0;
    }

    private static Map<String, Object> idDetails(String field, Long id) {
        if (id == null) {
            return Map.of("field", field);
        }
        return Map.of(field, id);
    }
}
