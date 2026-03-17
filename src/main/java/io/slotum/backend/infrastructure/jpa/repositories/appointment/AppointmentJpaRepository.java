package io.slotum.backend.infrastructure.jpa.repositories.appointment;

import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentJpa, Long> {
}