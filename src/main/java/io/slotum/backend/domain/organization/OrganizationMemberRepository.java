package io.slotum.backend.domain.organization;

import io.slotum.backend.domain.specialist.Specialist;

import java.util.List;

public interface OrganizationMemberRepository {
    boolean exists(Long organizationId, Long specialistUserId);

    OrganizationMember save(Long organizationId, Long specialistUserId);

    OrganizationMember delete(Long organizationId, Long specialistUserId);

    List<Specialist> findSpecialistsByOrganizationId(Long organizationId);

    List<Organization> findOrganizationsBySpecialistUserId(Long specialistUserId);
}
