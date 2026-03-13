package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;

import java.util.Optional;

public interface OrganizationJpaRepository {
    Optional<OrganizationJpa> findByName(String name);
}
