package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class DeleteByIdAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;

    public DeleteByIdAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment execute(Long id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_NOT_FOUND,
                    "Appointment not found",
                    Map.of("id", id)
            );
        }

        return appointmentRepository.deleteById(id);
    }
}
