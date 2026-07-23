package io.slotum.backend.domain.organizationMember;

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

    /**
     * Создание члена организации
     * @param organizationId идентификатор организации
     * @param specialistUserId идентификатор пользователя этой организации
     * @return созданный член организации
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_ORGANIZATION_ID} — идентификатор организации null или не положительный</li>
     *          <li>{@code INVALID_SPECIALIST_USER_ID} — идентификатор пользователя null или не положительный</li>
     *      </ul>
     */
    public static OrganizationMember create(Long organizationId, Long specialistUserId) {
        return new OrganizationMember(organizationId, specialistUserId);
    }

    /* Getters */
    public Long getOrganizationId() {
        return organizationId;
    }

    public Long getSpecialistUserId() {
        return specialistUserId;
    }

    /* Validation */
    private static void validateOrganizationId(Long id) {
        if (id == null || id <= 0) {
            Map<String, Object> details =
                    id == null
                    ? Map.of()
                    : Map.of("organizationId", id);

            throw AppException.build(
                    ErrorCode.INVALID_ORGANIZATION_ID,
                    "Invalid organization id",
                    details
            );
        }
    }

    private static void validateSpecialistUserId(Long id) {
        if (id == null || id <= 0) {
            Map<String, Object> details =
                    id == null
                    ? Map.of()
                    : Map.of("specialistUserId", id);

            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_USER_ID,
                    "Invalid specialist userId",
                    details
            );
        }
    }
}
