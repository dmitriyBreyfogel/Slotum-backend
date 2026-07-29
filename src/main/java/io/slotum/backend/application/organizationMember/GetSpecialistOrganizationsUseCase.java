package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetSpecialistOrganizationsUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final SpecialistRepository specialistRepository;

    public GetSpecialistOrganizationsUseCase(
            OrganizationMemberRepository organizationMemberRepository,
            SpecialistRepository specialistRepository
    ) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.specialistRepository = specialistRepository;
    }

    public List<Organization> execute(Long specialistUserId) {
        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistUserId);
        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", specialistUserId)
            );
        }

        return organizationMemberRepository.findOrganizationsBySpecialistUserId(specialistUserId);
    }
}
