package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AcceptAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentRepository appointmentRepository;

    public AcceptAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public AppointmentRequest execute(Long appointmentRequestId, Long specialistUserId) {
        AppointmentRequest appointmentRequest = findAppointmentRequest(appointmentRequestId);
        LocalDateTime decidedAt = LocalDateTime.now();
        AppointmentRequest acceptedAppointmentRequest = appointmentRequest.accept(decidedAt);

        Appointment appointment = findAppointment(appointmentRequest.getAppointmentId());
        ensureSpecialistOwnsAppointment(appointment, specialistUserId);
        ensureAppointmentIsFree(appointment);

        appointmentRepository.save(appointment.book(appointmentRequest.getCustomerId()));
        AppointmentRequest savedAppointmentRequest = appointmentRequestRepository.save(acceptedAppointmentRequest);

        appointmentRequestRepository.findPendingByAppointmentId(appointment.getId()).stream()
                .filter(otherRequest -> !Objects.equals(otherRequest.getId(), appointmentRequest.getId()))
                .map(otherRequest -> otherRequest.reject(decidedAt))
                .forEach(appointmentRequestRepository::save);

        return savedAppointmentRequest;
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

    private static void ensureSpecialistOwnsAppointment(Appointment appointment, Long specialistUserId) {
        if (!appointment.getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot accept appointment request",
                    Map.of(
                            "appointmentId", appointment.getId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }
    }

    private static void ensureAppointmentIsFree(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.FREE) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE,
                    "Appointment is not free",
                    Map.of(
                            "appointmentId", appointment.getId(),
                            "status", appointment.getStatus()
                    )
            );
        }
    }
}
