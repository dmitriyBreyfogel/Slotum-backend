package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_members")
public class OrganizationMembeerJpaEntity {
    @EmbeddedId
    private OrganizationMemberId id;

    // Connections
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OrganizationJpaEntity organization;

    @ManyToOne
    @JoinColumn(name = "specialist_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private OrganizationJpaEntity specialist;

    protected OrganizationMembeerJpaEntity() {}

    // Getters
    public OrganizationMemberId getId() {
        return id;
    }

    public OrganizationJpaEntity getOrganization() {
        return organization;
    }

    public OrganizationJpaEntity getSpecialist() {
        return specialist;
    }

    // Composite key
    @Embeddable
    public static class OrganizationMemberId {
        private Long organizationId;
        private Long specialistId;

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
