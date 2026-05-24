package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class RejectAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentRepository appointmentRepository;

    public RejectAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public AppointmentRequest execute(Long appointmentRequestId, Long specialistUserId) {
        AppointmentRequest appointmentRequest = findAppointmentRequest(appointmentRequestId);
        Appointment appointment = findAppointment(appointmentRequest.getAppointmentId());

        if (!appointment.getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot reject appointment request",
                    Map.of(
                            "appointmentId", appointment.getId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }

        return appointmentRequestRepository.save(appointmentRequest.reject(LocalDateTime.now()));
    }

    private AppointmentRequest findAppointmentRequest(Long appointmentRequestId) {
        Optional<AppointmentRequest> appointmentRequest = appointmentRequestRepository.findById(appointmentRequestId);
        if (appointmentRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND,
                    "Appointment request not found",
                    Map.of("id", appointmentRequestId)
            );
        }
        return appointmentRequest.get();
    }

    private Appointment findAppointment(Long appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        if (appointment.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_NOT_FOUND,
                    "Appointment not found",
                    Map.of("id", appointmentId)
            );
        }
        return appointment.get();
    }
}
