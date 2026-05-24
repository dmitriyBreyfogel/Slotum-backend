package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetAppointmentRequestsByAppointmentUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentRepository appointmentRepository;

    public GetAppointmentRequestsByAppointmentUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<AppointmentRequest> execute(Long appointmentId, Long specialistUserId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_NOT_FOUND,
                    "Appointment not found",
                    Map.of("id", appointmentId)
            );
        }

        if (!appointment.get().getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot access appointment requests",
                    Map.of(
                            "appointmentId", appointmentId,
                            "specialistUserId", specialistUserId
                    )
            );
        }

        return appointmentRequestRepository.findByAppointmentId(appointmentId);
    }
}
