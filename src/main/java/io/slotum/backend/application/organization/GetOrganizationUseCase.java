package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetOrganizationUseCase {
    private final OrganizationRepository organizationRepository;

    public GetOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization execute(Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);

        if (organization.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_NOT_FOUND,
                    "Organization not found",
                    Map.of("id", id)
            );
        }

        return organization.get();
    }
}
