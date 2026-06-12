package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
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
    private final SlotRepository slotRepository;

    public RejectAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            SlotRepository slotRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.slotRepository = slotRepository;
    }

    public AppointmentRequest execute(Long appointmentRequestId, Long specialistUserId) {
        AppointmentRequest appointmentRequest = findAppointmentRequest(appointmentRequestId);
        Slot slot = findSlot(appointmentRequest.getSlotId());

        if (!slot.getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot reject appointment request",
                    Map.of(
                            "slotId", slot.getId(),
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
                    "Slot request not found",
                    Map.of("id", appointmentRequestId)
            );
        }
        return appointmentRequest.get();
    }

    private Slot findSlot(Long slotId) {
        Optional<Slot> slot = slotRepository.findById(slotId);
        if (slot.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_NOT_FOUND,
                    "Slot not found",
                    Map.of("id", slotId)
            );
        }
        return slot.get();
    }
}
