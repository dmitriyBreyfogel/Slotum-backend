package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllAppointmentsUseCase {
    private final AppointmentRepository appointmentRepository;

    public GetAllAppointmentsUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> execute() {
        return appointmentRepository.findAll();
    }
}
