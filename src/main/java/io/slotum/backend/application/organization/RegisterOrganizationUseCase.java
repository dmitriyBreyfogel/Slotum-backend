package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RegisterOrganizationUseCase {
    private final OrganizationRepository organizationRepository;

    public RegisterOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Result execute(Command command) {
        Organization organizationToSave = Organization.create(
                null,
                command.name,
                command.description
        );

        String normalizedName = organizationToSave.getName();
        Optional<Organization> existingOrganization = organizationRepository.findByName(normalizedName);
        if (existingOrganization != null && existingOrganization.isPresent()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_ALREADY_EXISTS,
                    "Organization with name " + normalizedName + " already exists",
                    Map.of("name", normalizedName)
            );
        }

        Organization organization = organizationRepository.save(organizationToSave);

        return new Result(
                organization.getId(),
                organization.getName()
        );
    }

    public record Command(String name, String description) {}

    public record Result(Long id, String name) {}
}
