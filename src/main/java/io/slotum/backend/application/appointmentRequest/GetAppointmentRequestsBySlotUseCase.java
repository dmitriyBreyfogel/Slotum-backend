package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetAppointmentRequestsBySlotUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final SlotRepository slotRepository;

    public GetAppointmentRequestsBySlotUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            SlotRepository slotRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.slotRepository = slotRepository;
    }

    public List<AppointmentRequest> execute(Long slotId, Long specialistUserId) {
        Optional<Slot> slot = slotRepository.findById(slotId);
        if (slot.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_NOT_FOUND,
                    "Slot not found",
                    Map.of("id", slotId)
            );
        }

        if (!slot.get().getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot access appointment requests",
                    Map.of(
                            "slotId", slotId,
                            "specialistUserId", specialistUserId
                    )
            );
        }

        return appointmentRequestRepository.findBySlotId(slotId);
    }
}
