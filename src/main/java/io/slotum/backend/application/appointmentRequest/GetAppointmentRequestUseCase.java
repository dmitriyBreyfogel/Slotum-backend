package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;

    public GetAppointmentRequestUseCase(AppointmentRequestRepository appointmentRequestRepository) {
        this.appointmentRequestRepository = appointmentRequestRepository;
    }

    public AppointmentRequest execute(Long id) {
        Optional<AppointmentRequest> appointmentRequest = appointmentRequestRepository.findById(id);

        if (appointmentRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND,
                    "Slot request not found",
                    Map.of("id", id)
            );
        }

        return appointmentRequest.get();
    }
}
