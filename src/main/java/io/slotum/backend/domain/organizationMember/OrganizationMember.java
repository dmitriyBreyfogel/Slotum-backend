package io.slotum.backend.domain.organizationMember;

public final class OrganizationMember {
    private final Long organizationId;
    private final Long specialistUserId;

    private OrganizationMember(final Long organizationId, final Long specialistUserId) {
        this.organizationId = organizationId;
        this.specialistUserId = specialistUserId;
    }

    /**
     * Создание члена организации
     * @param organizationId идентификатор организации
     * @param specialistUserId идентификатор пользователя этой организации
     * @return созданный член организации
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
}
