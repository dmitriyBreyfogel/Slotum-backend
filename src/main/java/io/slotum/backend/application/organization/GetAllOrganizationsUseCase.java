package io.slotum.backend.application.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetAllOrganizationsUseCase {
    private final OrganizationRepository organizationRepository;

    public GetAllOrganizationsUseCase(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<Organization> execute() {
        return organizationRepository.findAll();
    }
}
