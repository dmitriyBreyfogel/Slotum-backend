package io.slotum.backend.application.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RemoveSpecialistFromOrganizationUseCase {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final SpecialistRepository specialistRepository;

    public RemoveSpecialistFromOrganizationUseCase(
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRepository organizationRepository,
            SpecialistRepository specialistRepository
    ) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.specialistRepository = specialistRepository;
    }

    public OrganizationMember execute(Long organizationId, Long specialistUserId) {
        OrganizationMember.create(organizationId, specialistUserId);

        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization == null || organization.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_NOT_FOUND,
                    "Organization not found",
                    Map.of("organizationId", organizationId)
            );
        }

        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistUserId);
        if (specialist == null || specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", specialistUserId)
            );
        }

        if (!organizationMemberRepository.exists(organizationId, specialistUserId)) {
            throw membershipNotFound(organizationId, specialistUserId);
        }

        OrganizationMember deleted = organizationMemberRepository.delete(organizationId, specialistUserId);
        if (deleted == null) {
            throw membershipNotFound(organizationId, specialistUserId);
        }

        return deleted;
    }

    private static AppException membershipNotFound(Long organizationId, Long specialistUserId) {
        return AppException.build(
                ErrorCode.ORGANIZATION_MEMBERSHIP_NOT_FOUND,
                "Organization membership not found",
                Map.of(
                        "organizationId", organizationId,
                        "specialistUserId", specialistUserId
                )
        );
    }
}
