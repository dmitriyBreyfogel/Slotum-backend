package io.slotum.backend.application.usecase.slotBookingRequest;

import io.slotum.backend.application.events.booking.BookingRequestCreatedEvent;
import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CreateSlotBookingRequestUseCase {
    private final ApplicationEventPublisher eventPublisher;
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public CreateSlotBookingRequestUseCase(
            ApplicationEventPublisher eventPublisher,
            SlotBookingRequestRepository slotBookingRequestRepository,
            SlotRepository slotRepository,
            UserRepository userRepository
    ) {
        this.eventPublisher = eventPublisher;
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SlotBookingRequest execute(Command command) {
        SlotBookingRequest slotBookingRequestToSave = createSlotBookingRequest(command);
        Slot slot = findSlot(command.slotId);
        User customer = findCustomer(command.customerId);

        ensureSlotIsFree(slot);
        ensureNoPendingRequestExists(command.slotId, command.customerId);

        SlotBookingRequest saved = slotBookingRequestRepository.save(slotBookingRequestToSave);

        eventPublisher.publishEvent(
                new BookingRequestCreatedEvent(
                        saved.getId(),
                        saved.getSlotId(),
                        saved.getCustomerId(),
                        slot.getSpecialistUserId(),
                        customer.getFirstName(),
                        customer.getSurname(),
                        slot.getStartsAt()
                )
        );

        return saved;
    }

    private static SlotBookingRequest createSlotBookingRequest(Command command) {
        return SlotBookingRequest.create(
                command.slotId,
                command.customerId,
                command.message,
                LocalDateTime.now()
        );
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

    private User findCustomer(Long customerId) {
        Optional<User> customer = userRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User customer not found",
                    Map.of("customerId", customerId)
            );
        }
        return customer.get();
    }

    private static void ensureSlotIsFree(Slot slot) {
        if (slot.getStatus() != SlotStatus.FREE) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_SLOT_NOT_FREE,
                    "Slot is not free",
                    Map.of(
                            "slotId", slot.getId(),
                            "status", slot.getStatus()
                    )
            );
        }
    }


    private void ensureNoPendingRequestExists(Long slotId, Long customerId) {
        if (slotBookingRequestRepository.existsPendingBySlotIdAndCustomerId(
                slotId,
                customerId
        )) {
            throw AppException.build(
                    ErrorCode.SLOT_BOOKING_REQUEST_ALREADY_EXISTS,
                    "Pending slot booking request already exists",
                    Map.of(
                            "slotId", slotId,
                            "customerId", customerId
                    )
            );
        }
    }

    public record Command(
            Long slotId,
            Long customerId,
            String message
    ) {}

}
