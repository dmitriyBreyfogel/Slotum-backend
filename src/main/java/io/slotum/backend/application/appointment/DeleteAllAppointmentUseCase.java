package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;

    public DeleteAllAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public void execute() {
        appointmentRepository.deleteAll();
    }
}
