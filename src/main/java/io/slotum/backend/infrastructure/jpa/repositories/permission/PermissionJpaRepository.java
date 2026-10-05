package io.slotum.backend.infrastructure.jpa.repositories.permission;

import io.slotum.backend.infrastructure.jpa.entities.PermissionJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpa, Long> {
    Optional<PermissionJpa> findByCode(String code);
}
