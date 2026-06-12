package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetSlotBookingRequestsBySlotUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;

    public GetSlotBookingRequestsBySlotUseCase(
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository
    ) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
    }

    public List<SlotBookingRequest> execute(Long slotId, Long specialistUserId) {
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
                    ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN,
                    "Specialist cannot access slot booking requests",
                    Map.of(
                            "slotId", slotId,
                            "specialistUserId", specialistUserId
                    )
            );
        }

        return slotBookingRequestRepository.findBySlotId(slotId);
    }
}
