package io.slotum.backend.application.usecase.slotBookingRequest;

import io.slotum.backend.application.events.booking.BookingRejectedEvent;
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
public class RejectSlotBookingRequestUseCase {
    private final ApplicationEventPublisher eventPublisher;

    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;

    public RejectSlotBookingRequestUseCase(
            ApplicationEventPublisher eventPublisher,
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository
    ) {
        this.eventPublisher = eventPublisher;
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional
    public SlotBookingRequest execute(Long slotBookingRequestId, Long specialistUserId) {
        SlotBookingRequest slotBookingRequest = findSlotBookingRequest(slotBookingRequestId);
        LocalDateTime decidedAt = LocalDateTime.now();
        SlotBookingRequest rejectedSlotBookingRequest = slotBookingRequest.reject(decidedAt);
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

        boolean rejected = slotBookingRequestRepository.rejectIfPending(
                slotBookingRequest.getId(),
                decidedAt
        );

        if (!rejected) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING,
                    "Slot request is not pending",
                    Map.of("id", slotBookingRequest.getId())
            );
        }

        eventPublisher.publishEvent(
                new BookingRejectedEvent(
                        slotBookingRequest.getId(),
                        slotBookingRequest.getCustomerId(),
                        slot.getStartsAt()
                )
        );

        return rejectedSlotBookingRequest;
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
