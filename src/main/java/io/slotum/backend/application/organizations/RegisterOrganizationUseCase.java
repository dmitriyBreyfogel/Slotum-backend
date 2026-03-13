package io.slotum.backend.application.organizations;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegisterOrganizationUseCase {
    private OrganizationRepository organizationRepository;

    public RegisterOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Result execute(Command command) {
        if (organizationRepository.findByName(command.name) != null) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_ALREADY_EXISTS,
                    "Organization with name " + command.name + " already exists",
                    Map.of("name", command.name)
            );
        }

        Organization organization = organizationRepository.save(
                Organization.create(
                    null,
                    command.name,
                    command.description
                )
        );

        return new Result(
                organization.getId(),
                organization.getName()
        );
    }

    public record Command(String name, String description) {}

    public record Result(Long id, String name) {}
}
