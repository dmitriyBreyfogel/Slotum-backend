package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.OrganizationMemberRepository;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrganizationSpecialistsUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;

    public GetOrganizationSpecialistsUseCase(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public List<Specialist> execute(Long organizationId) {
        return organizationMemberRepository.findSpecialistsByOrganizationId(organizationId);
    }
}
