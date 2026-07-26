package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeleteByIdOrganizationUseCase {
    private final OrganizationRepository organizationRepository;

    public DeleteByIdOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization execute(Long id) {
        return organizationRepository.deleteById(id).orElseThrow(() -> AppException.build(
                ErrorCode.ORGANIZATION_NOT_FOUND,
                "Organization not found",
                Map.of("id", id)
        ));
    }
}
