package io.slotum.backend.application.usecase.slotBookingRequest;

import io.slotum.backend.application.events.booking.BookingCancelledEvent;
import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CancelSlotBookingRequestUseCase {
    private final ApplicationEventPublisher eventPublisher;

    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;

    public CancelSlotBookingRequestUseCase(
            ApplicationEventPublisher eventPublisher,
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository
    ) {
        this.eventPublisher = eventPublisher;
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
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
        Slot slot = getSlot(cancelledSlotBookingRequest.getSlotId());
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

        eventPublisher.publishEvent(
                new BookingCancelledEvent(
                        cancelledSlotBookingRequest.getId(),
                        slot.getSpecialistUserId(),
                        slot.getStartsAt()
                )
        );

        return cancelledSlotBookingRequest;
    }

    private Slot getSlot(Long slotBookingRequestId) {
        Optional<Slot> result = slotRepository.findById(slotBookingRequestId);

        if (result.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_NOT_FOUND,
                    "Slot not found by slotBookingRequestId",
                    Map.of("slotBookingRequestId", slotBookingRequestId)
            );
        }

        return result.get();
    }
}
