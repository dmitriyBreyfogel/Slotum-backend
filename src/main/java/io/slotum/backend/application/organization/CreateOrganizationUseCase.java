package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CreateOrganizationUseCase {
    private final OrganizationRepository organizationRepository;

    public CreateOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization execute(Command command) {
        Organization organizationToSave = Organization.create(
                command.name,
                command.description
        );

        String normalizedOrganizationName = organizationToSave.getName();
        Optional<Organization> existingOrganization = organizationRepository.findByName(normalizedOrganizationName);
        if (existingOrganization != null && existingOrganization.isPresent()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_ALREADY_EXISTS,
                    "Organization already exists",
                    Map.of("name", normalizedOrganizationName)
            );
        }

        return organizationRepository.save(organizationToSave);
    }

    public record Command(
            String name,
            String description
    ) {}

}
