package io.slotum.backend.infrastructure.jpa.repositories.role;

import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.infrastructure.jpa.entities.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleJpa, Long> {
    Optional<RoleJpa> findByName(RoleNames name);
}
