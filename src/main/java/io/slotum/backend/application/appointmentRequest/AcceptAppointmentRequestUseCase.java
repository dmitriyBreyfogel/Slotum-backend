package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class AcceptAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final SlotRepository slotRepository;

    public AcceptAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            SlotRepository slotRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.slotRepository = slotRepository;
    }

    @Transactional
    public AppointmentRequest execute(Long appointmentRequestId, Long specialistUserId) {
        AppointmentRequest appointmentRequest = findAppointmentRequest(appointmentRequestId);
        LocalDateTime decidedAt = LocalDateTime.now();
        AppointmentRequest acceptedAppointmentRequest = appointmentRequest.accept(decidedAt);

        Slot slot = findSlot(appointmentRequest.getSlotId());
        ensureSpecialistOwnsSlot(slot, specialistUserId);
        ensureSlotIsFree(slot);

        slotRepository.save(slot.book(appointmentRequest.getCustomerId()));
        AppointmentRequest savedAppointmentRequest = appointmentRequestRepository.save(acceptedAppointmentRequest);

        appointmentRequestRepository.findPendingBySlotId(slot.getId()).stream()
                .filter(otherRequest -> !Objects.equals(otherRequest.getId(), appointmentRequest.getId()))
                .map(otherRequest -> otherRequest.reject(decidedAt))
                .forEach(appointmentRequestRepository::save);

        return savedAppointmentRequest;
    }

    private AppointmentRequest findAppointmentRequest(Long appointmentRequestId) {
        Optional<AppointmentRequest> appointmentRequest = appointmentRequestRepository.findById(appointmentRequestId);
        if (appointmentRequest.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND,
                    "Slot request not found",
                    Map.of("id", appointmentRequestId)
            );
        }
        return appointmentRequest.get();
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
                    ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN,
                    "Specialist cannot accept appointment request",
                    Map.of(
                            "slotId", slot.getId(),
                            "specialistUserId", specialistUserId
                    )
            );
        }
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
}
