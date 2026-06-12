package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CreateAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    public CreateAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            SlotRepository slotRepository,
            UserRepository userRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
    }

    public AppointmentRequest execute(Command command) {
        AppointmentRequest appointmentRequestToSave = createAppointmentRequest(command);
        Slot slot = findSlot(command.slotId);

        ensureSlotIsFree(slot);
        ensureCustomerExists(command.customerId);
        ensureNoPendingRequestExists(command.slotId, command.customerId);

        return appointmentRequestRepository.save(appointmentRequestToSave);
    }

    private static AppointmentRequest createAppointmentRequest(Command command) {
        return AppointmentRequest.create(
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

    private static void ensureSlotIsFree(Slot slot) {
        if (slot.getStatus() != SlotStatus.FREE) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_SLOT_NOT_FREE,
                    "Slot is not free",
                    Map.of(
                            "slotId", slot.getId(),
                            "status", slot.getStatus()
                    )
            );
        }
    }

    private void ensureCustomerExists(Long customerId) {
        if (userRepository.findById(customerId).isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User customer not found",
                    Map.of("customerId", customerId)
            );
        }
    }

    private void ensureNoPendingRequestExists(Long slotId, Long customerId) {
        if (appointmentRequestRepository.existsPendingBySlotIdAndCustomerId(
                slotId,
                customerId
        )) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_ALREADY_EXISTS,
                    "Pending appointment request already exists",
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
