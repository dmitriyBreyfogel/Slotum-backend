package io.slotum.backend.infrastructure.jpa.repositories.slot;

import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SlotJpaRepository extends JpaRepository<SlotJpa, Long> {
    boolean existsBySpecialistUserIdAndStatusNotAndStartsAtLessThanAndEndsAtGreaterThan(
            Long specialistUserId,
            SlotStatus status,
            LocalDateTime endsAt,
            LocalDateTime startsAt
    );
}