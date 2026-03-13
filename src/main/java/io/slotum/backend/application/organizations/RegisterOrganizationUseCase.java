package io.slotum.backend.application.organizations;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterOrganizationUseCase {
    private OrganizationRepository organizationRepository;

    public RegisterOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Result execute(Command command) {
        Organization organization = Organization.create(
                null,
                command.name,
                command.description
        );

        organizationRepository.save(organization);

        return new Result(
                organization.getId(),
                organization.getName()
        );
    }

    public record Command(String name, String description) {}

    public record Result(Long id, String name) {}
}
