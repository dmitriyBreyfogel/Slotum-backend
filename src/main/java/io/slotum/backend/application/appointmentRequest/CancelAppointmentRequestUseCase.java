package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CancelAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;

    public CancelAppointmentRequestUseCase(AppointmentRequestRepository appointmentRequestRepository) {
        this.appointmentRequestRepository = appointmentRequestRepository;
    }

    public AppointmentRequest execute(Long appointmentRequestId, Long customerId) {
        Optional<AppointmentRequest> appointmentRequest = appointmentRequestRepository.findById(appointmentRequestId);
        if (appointmentRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND,
                    "Appointment request not found",
                    Map.of("id", appointmentRequestId)
            );
        }

        if (!appointmentRequest.get().getCustomerId().equals(customerId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Customer cannot cancel appointment request",
                    Map.of(
                            "appointmentRequestId", appointmentRequestId,
                            "customerId", customerId
                    )
            );
        }

        return appointmentRequestRepository.save(appointmentRequest.get().cancel(LocalDateTime.now()));
    }
}
