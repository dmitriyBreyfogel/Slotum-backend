package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_members")
public class OrganizationMemberJpa {
    @EmbeddedId
    private OrganizationMemberId id;

    // Communications
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OrganizationJpa organization;

    @ManyToOne
    @JoinColumn(name = "specialist_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private SpecialistJpa specialist;

    protected OrganizationMemberJpa() {}

    public OrganizationMemberJpa(Long organizationId, Long specialistUserId) {
        this.id = new OrganizationMemberId(organizationId, specialistUserId);
    }

    // Getters
    public OrganizationMemberId getId() {
        return id;
    }

    public OrganizationJpa getOrganization() {
        return organization;
    }

    public SpecialistJpa getSpecialist() {
        return specialist;
    }

    // Composite key
    @Embeddable
    public static class OrganizationMemberId {
        @Column(name = "organization_id")
        private Long organizationId;

        @Column(name = "specialist_id")
        private Long specialistId;

        protected OrganizationMemberId() {}

        public OrganizationMemberId(Long organizationId, Long specialistId) {
            this.organizationId = organizationId;
            this.specialistId = specialistId;
        }

        public Long getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(Long organizationId) {
            this.organizationId = organizationId;
        }

        public Long getSpecialistId() {
            return specialistId;
        }

        public void setSpecialistId(Long specialistId) {
            this.specialistId = specialistId;
        }
    }
}
