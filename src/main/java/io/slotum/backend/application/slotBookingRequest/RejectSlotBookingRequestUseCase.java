package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class RejectSlotBookingRequestUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;

    public RejectSlotBookingRequestUseCase(
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository
    ) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
    }

    public SlotBookingRequest execute(Long slotBookingRequestId, Long specialistUserId) {
        SlotBookingRequest slotBookingRequest = findSlotBookingRequest(slotBookingRequestId);
        Slot slot = findSlot(slotBookingRequest.getSlotId());

        if (!slot.getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN,
                    "Specialist cannot reject slot booking request",
                    Map.of(
                            "slotId", slot.getId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }

        return slotBookingRequestRepository.save(slotBookingRequest.reject(LocalDateTime.now()));
    }

    private SlotBookingRequest findSlotBookingRequest(Long slotBookingRequestId) {
        Optional<SlotBookingRequest> slotBookingRequest = slotBookingRequestRepository.findById(slotBookingRequestId);
        if (slotBookingRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND,
                    "Slot request not found",
                    Map.of("id", slotBookingRequestId)
            );
        }
        return slotBookingRequest.get();
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
