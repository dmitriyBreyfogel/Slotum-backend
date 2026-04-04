package io.slotum.backend.domain.appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Optional<Appointment> findById(Long id);

    Appointment save(Appointment appointment);

    List<Appointment> findAll();

    Appointment deleteById(Long id);
}