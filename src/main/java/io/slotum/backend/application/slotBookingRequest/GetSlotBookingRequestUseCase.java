package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetSlotBookingRequestUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;

    public GetSlotBookingRequestUseCase(SlotBookingRequestRepository slotBookingRequestRepository) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
    }

    public SlotBookingRequest execute(Long id) {
        Optional<SlotBookingRequest> slotBookingRequest = slotBookingRequestRepository.findById(id);

        if (slotBookingRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND,
                    "Slot request not found",
                    Map.of("id", id)
            );
        }

        return slotBookingRequest.get();
    }
}
