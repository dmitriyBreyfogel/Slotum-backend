package io.slotum.backend.infrastructure.jpa.repositories.resource;

import io.slotum.backend.infrastructure.jpa.entities.ResourceJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceJpaRepository extends JpaRepository<ResourceJpa, Long> {
}
