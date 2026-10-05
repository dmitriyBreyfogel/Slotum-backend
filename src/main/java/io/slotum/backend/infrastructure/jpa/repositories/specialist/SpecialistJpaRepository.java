package io.slotum.backend.infrastructure.jpa.repositories.specialist;

import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistJpaRepository extends JpaRepository<SpecialistJpa, Long> {
    Optional<SpecialistJpa> findByUserId(long userId);
}
