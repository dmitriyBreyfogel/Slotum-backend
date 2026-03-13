package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpa, Long> {
    Optional<OrganizationJpa> findByName(String name);
}
