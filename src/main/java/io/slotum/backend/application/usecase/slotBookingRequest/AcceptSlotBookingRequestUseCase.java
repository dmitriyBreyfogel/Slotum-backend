package io.slotum.backend.application.usecase.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AcceptSlotBookingRequestUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;

    public AcceptSlotBookingRequestUseCase(
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository
    ) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional
    public SlotBookingRequest execute(Long slotBookingRequestId, Long specialistUserId) {
        SlotBookingRequest slotBookingRequest = findSlotBookingRequest(slotBookingRequestId);
        LocalDateTime decidedAt = LocalDateTime.now();

        SlotBookingRequest acceptedSlotBookingRequest = slotBookingRequest.accept(decidedAt);

        Slot slot = findSlot(slotBookingRequest.getSlotId());
        ensureSpecialistOwnsSlot(slot, specialistUserId);

        boolean booked = slotRepository.bookIfFree(
                slot.getId(),
                specialistUserId,
                slotBookingRequest.getCustomerId()
        );

        if (!booked) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_SLOT_NOT_FREE,
                    "Slot not free",
                    Map.of(
                            "slotId", slotBookingRequest.getSlotId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }

        boolean accepted = slotBookingRequestRepository.acceptIfPending(
                slotBookingRequest.getId(),
                decidedAt
        );

        if (!accepted) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING,
                    "Slot request is not pending",
                    Map.of("id", slotBookingRequest.getId())
            );
        }

        slotBookingRequestRepository.findPendingBySlotId(slot.getId()).stream()
                .filter(otherRequest -> !Objects.equals(otherRequest.getId(), slotBookingRequest.getId()))
                .map(otherRequest -> otherRequest.reject(decidedAt))
                .forEach(slotBookingRequestRepository::save);

        return acceptedSlotBookingRequest;
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

    private static void ensureSpecialistOwnsSlot(Slot slot, Long specialistUserId) {
        if (!slot.getSpecialistUserId().equals(specialistUserId)) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN,
                    "Specialist cannot accept slot booking request",
                    Map.of(
                            "slotId", slot.getId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }
    }
}
