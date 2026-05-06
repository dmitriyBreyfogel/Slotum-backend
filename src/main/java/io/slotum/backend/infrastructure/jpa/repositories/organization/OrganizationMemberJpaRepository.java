package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberJpa, OrganizationMemberJpa.OrganizationMemberId> {
    boolean existsByIdOrganizationIdAndIdSpecialistId(Long organizationId, Long specialistId);

    List<OrganizationMemberJpa> findAllByIdOrganizationId(Long organizationId);

    List<OrganizationMemberJpa> findAllByIdSpecialistId(Long specialistId);
}
