package io.slotum.backend.infrastructure.jpa.repositories.appointment;

import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpa, Long> {
    boolean existsBySpecialistUserIdAndStatusNotAndStartsAtLessThanAndEndsAtGreaterThan(
            Long specialistUserId,
            AppointmentStatus status,
            LocalDateTime endsAt,
            LocalDateTime startsAt
    );
}