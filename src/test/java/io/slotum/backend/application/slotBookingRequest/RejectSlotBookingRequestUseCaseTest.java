package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RejectSlotBookingRequestUseCaseTest {

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectSlotBookingRequestUseCase useCase = new RejectSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND, ex.getCode());
        verify(slotBookingRequestRepository).findById(1L);
        verifyNoInteractions(slotRepository);
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_FORBIDDEN if specialist does not own slot")
    void throwsIfSpecialistDoesNotOwnSlot() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectSlotBookingRequestUseCase useCase = new RejectSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 99L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("specialistUserId"));
        verify(slotBookingRequestRepository).findById(1L);
        verify(slotRepository).findById(5L);
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_NOT_PENDING if atomic reject fails")
    void throwsIfAtomicRejectFails() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectSlotBookingRequestUseCase useCase = new RejectSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(slotBookingRequestRepository.rejectIfPending(eq(1L), any(LocalDateTime.class))).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(slotBookingRequestRepository).findById(1L);
        verify(slotRepository).findById(5L);
        verify(slotBookingRequestRepository).rejectIfPending(eq(1L), any(LocalDateTime.class));
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository);
    }

    @Test
    @DisplayName("execute: rejects pending request")
    void rejectsPendingRequest() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        RejectSlotBookingRequestUseCase useCase = new RejectSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(slotBookingRequestRepository.rejectIfPending(eq(1L), any(LocalDateTime.class))).thenReturn(true);

        SlotBookingRequest result = useCase.execute(1L, 10L);

        assertEquals(SlotBookingRequestStatus.REJECTED, result.getStatus());
        assertNotNull(result.getDecidedAt());
        verify(slotBookingRequestRepository).findById(1L);
        verify(slotRepository).findById(5L);
        verify(slotBookingRequestRepository).rejectIfPending(eq(1L), any(LocalDateTime.class));
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository);
    }

    private static SlotBookingRequest pendingRequest() {
        return SlotBookingRequest.restore(
                1L,
                5L,
                20L,
                SlotBookingRequestStatus.PENDING,
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
