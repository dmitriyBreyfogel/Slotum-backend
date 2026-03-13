package io.slotum.backend.domain.organization;

import io.slotum.backend.domain.user.User;

import java.util.Optional;

public interface OrganizationRepository {
    Optional<Organization> findById(Long id);

    Optional<Organization> findByName(String name);

    Organization save(Organization organization);
}
