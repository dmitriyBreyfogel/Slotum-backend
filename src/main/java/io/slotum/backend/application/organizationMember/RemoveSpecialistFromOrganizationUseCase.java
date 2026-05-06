package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.OrganizationMember;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveSpecialistFromOrganizationUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;

    public RemoveSpecialistFromOrganizationUseCase(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public OrganizationMember execute(Long organizationId, Long specialistUserId) {
        return organizationMemberRepository.delete(organizationId, specialistUserId);
    }
}
