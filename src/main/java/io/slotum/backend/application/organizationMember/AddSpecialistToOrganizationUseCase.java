package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.OrganizationMember;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import org.springframework.stereotype.Service;

@Service
public class AddSpecialistToOrganizationUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;

    public AddSpecialistToOrganizationUseCase(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public OrganizationMember execute(Long organizationId, Long specialistUserId) {
        return organizationMemberRepository.save(organizationId, specialistUserId);
    }
}
