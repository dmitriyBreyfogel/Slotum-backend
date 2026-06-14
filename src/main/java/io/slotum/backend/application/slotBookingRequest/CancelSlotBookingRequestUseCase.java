package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CancelSlotBookingRequestUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;

    public CancelSlotBookingRequestUseCase(SlotBookingRequestRepository slotBookingRequestRepository) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
    }

    @Transactional
    public SlotBookingRequest execute(Long slotBookingRequestId, Long customerId) {
        Optional<SlotBookingRequest> slotBookingRequest = slotBookingRequestRepository.findById(slotBookingRequestId);
        if (slotBookingRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND,
                    "Slot request not found",
                    Map.of("id", slotBookingRequestId)
            );
        }

        if (!slotBookingRequest.get().getCustomerId().equals(customerId)) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN,
                    "Customer cannot cancel slot booking request",
                    Map.of(
                            "slotBookingRequestId", slotBookingRequestId,
                            "customerId", customerId
                    )
            );
        }

        LocalDateTime decidedAt = LocalDateTime.now();
        SlotBookingRequest cancelledSlotBookingRequest = slotBookingRequest.get().cancel(decidedAt);
        boolean cancelled = slotBookingRequestRepository.cancelIfPending(
                slotBookingRequest.get().getId(),
                decidedAt
        );

        if (!cancelled) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING,
                    "Slot request is not pending",
                    Map.of("id", slotBookingRequest.get().getId())
            );
        }

        return cancelledSlotBookingRequest;
    }
}
