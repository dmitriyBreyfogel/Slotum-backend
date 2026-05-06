package io.slotum.backend.domain.organization;

import io.slotum.backend.domain.specialist.Specialist;

import java.util.List;

public interface OrganizationMemberRepository {
    boolean exists(Long organizationId, Long specialistUserId);

    void add(Long organizationId, Long specialistUserId);

    void remove(Long organizationId, Long specialistUserId);

    List<Specialist> findSpecialistsByOrganizationId(Long organizationId);

    List<Organization> findOrganizationsBySpecialistUserId(Long specialistUserId);
}
