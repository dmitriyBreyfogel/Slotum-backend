package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RejectAppointmentRequestUseCaseTest {

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectAppointmentRequestUseCase useCase = new RejectAppointmentRequestUseCase(
                appointmentRequestRepository,
                slotRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND, ex.getCode());
        verify(appointmentRequestRepository).findById(1L);
        verifyNoInteractions(slotRepository);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_FORBIDDEN if specialist does not own slot")
    void throwsIfSpecialistDoesNotOwnSlot() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectAppointmentRequestUseCase useCase = new RejectAppointmentRequestUseCase(
                appointmentRequestRepository,
                slotRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 99L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("specialistUserId"));
        verify(appointmentRequestRepository).findById(1L);
        verify(slotRepository).findById(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, slotRepository);
    }

    @Test
    @DisplayName("execute: rejects pending request")
    void rejectsPendingRequest() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectAppointmentRequestUseCase useCase = new RejectAppointmentRequestUseCase(
                appointmentRequestRepository,
                slotRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(appointmentRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentRequest result = useCase.execute(1L, 10L);

        assertEquals(AppointmentRequestStatus.REJECTED, result.getStatus());
        assertNotNull(result.getDecidedAt());
        verify(appointmentRequestRepository).findById(1L);
        verify(slotRepository).findById(5L);
        verify(appointmentRequestRepository).save(any());
        verifyNoMoreInteractions(appointmentRequestRepository, slotRepository);
    }

    private static AppointmentRequest pendingRequest() {
        return AppointmentRequest.restore(
                1L,
                5L,
                20L,
                AppointmentRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }

    private static Slot freeSlot() {
        return Slot.restore(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.FREE,
                10L,
                null,
                30L
        );
    }
}
