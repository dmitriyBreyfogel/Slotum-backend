package io.slotum.backend.application.usecase.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class CreateMyOrganizationUseCase {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final SpecialistRepository specialistRepository;

    public CreateMyOrganizationUseCase(
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            SpecialistRepository specialistRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.specialistRepository = specialistRepository;
    }

    @Transactional
    public Organization execute(Command command) {
        Organization organizationToSave = Organization.create(
                command.name,
                command.description
        );

        String normalizedOrganizationName = organizationToSave.getName();
        Optional<Organization> existingOrganization = organizationRepository.findByName(normalizedOrganizationName);
        if (existingOrganization.isPresent()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_ALREADY_EXISTS,
                    "Organization already exists",
                    Map.of("name", normalizedOrganizationName)
            );
        }

        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(command.specialistUserId);
        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", command.specialistUserId)
            );
        }

        Organization savedOrganization = organizationRepository.save(organizationToSave);
        OrganizationMember organizationMember = OrganizationMember.create(
                savedOrganization.getId(),
                command.specialistUserId
        );
        organizationMemberRepository.save(
                organizationMember.getOrganizationId(),
                organizationMember.getSpecialistUserId()
        );

        return savedOrganization;
    }

    public record Command(
            Long specialistUserId,
            String name,
            String description
    ) {}

}
