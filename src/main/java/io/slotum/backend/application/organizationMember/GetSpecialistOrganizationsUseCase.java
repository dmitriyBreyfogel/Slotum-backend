package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetSpecialistOrganizationsUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;

    public GetSpecialistOrganizationsUseCase(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public List<Organization> execute(Long specialistUserId) {
        return organizationMemberRepository.findOrganizationsBySpecialistUserId(specialistUserId);
    }
}
