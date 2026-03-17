package io.slotum.backend.domain.appointment;

import java.util.Optional;

public interface AppointmentRepository {
    Optional<Appointment> findById(Long id);

    Appointment save(Appointment appointment);
}