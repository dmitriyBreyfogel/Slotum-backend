package io.slotum.backend.application.slotBookingRequest;

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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CancelSlotBookingRequestUseCaseTest {

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        CancelSlotBookingRequestUseCase useCase = new CancelSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 20L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND, ex.getCode());
        verify(slotBookingRequestRepository).findById(1L);
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_FORBIDDEN if customer does not own request")
    void throwsIfCustomerDoesNotOwnRequest() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        CancelSlotBookingRequestUseCase useCase = new CancelSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 99L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("customerId"));
        verify(slotBookingRequestRepository).findById(1L);
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_NOT_PENDING if atomic cancel fails")
    void throwsIfAtomicCancelFails() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        CancelSlotBookingRequestUseCase useCase = new CancelSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotBookingRequestRepository.cancelIfPending(eq(1L), any(LocalDateTime.class))).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 20L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_PENDING, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(slotBookingRequestRepository).findById(1L);
        verify(slotBookingRequestRepository).cancelIfPending(eq(1L), any(LocalDateTime.class));
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    @Test
    @DisplayName("execute: cancels pending request")
    void cancelsPendingRequest() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        CancelSlotBookingRequestUseCase useCase = new CancelSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(slotBookingRequestRepository.cancelIfPending(eq(1L), any(LocalDateTime.class))).thenReturn(true);

        SlotBookingRequest result = useCase.execute(1L, 20L);

        assertEquals(SlotBookingRequestStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getDecidedAt());
        verify(slotBookingRequestRepository).findById(1L);
        verify(slotBookingRequestRepository).cancelIfPending(eq(1L), any(LocalDateTime.class));
        verifyNoMoreInteractions(slotBookingRequestRepository);
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
}
