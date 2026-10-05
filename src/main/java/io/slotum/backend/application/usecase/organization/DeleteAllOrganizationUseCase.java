package io.slotum.backend.application.usecase.organization;

import io.slotum.backend.domain.organization.OrganizationRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllOrganizationUseCase {
    private final OrganizationRepository organizationRepository;

    public DeleteAllOrganizationUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public void execute() {
        organizationRepository.deleteAll();
    }
}
