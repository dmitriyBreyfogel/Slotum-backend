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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetSlotBookingRequestUseCaseTest {

    @Test
    @DisplayName("execute: returns found request")
    void returnsFoundRequest() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        GetSlotBookingRequestUseCase useCase = new GetSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.of(request()));

        SlotBookingRequest result = useCase.execute(1L);

        assertEquals(1L, result.getId());
        verify(slotBookingRequestRepository).findById(1L);
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        GetSlotBookingRequestUseCase useCase = new GetSlotBookingRequestUseCase(slotBookingRequestRepository);
        when(slotBookingRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_NOT_FOUND, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(slotBookingRequestRepository).findById(1L);
        verifyNoMoreInteractions(slotBookingRequestRepository);
    }

    private static SlotBookingRequest request() {
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
