package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetOrganizationSpecialistsUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;

    public GetOrganizationSpecialistsUseCase(
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRepository organizationRepository
    ) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<Specialist> execute(Long organizationId) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_NOT_FOUND,
                    "Organization not found",
                    Map.of("organizationId", organizationId)
            );
        }

        return organizationMemberRepository.findSpecialistsByOrganizationId(organizationId);
    }
}
