package io.slotum.backend.infrastructure.jpa.repositories;

import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpa, Long> {
    Optional<UserJpa> findByEmail(String email);

    boolean existsByEmail(String email);
}
