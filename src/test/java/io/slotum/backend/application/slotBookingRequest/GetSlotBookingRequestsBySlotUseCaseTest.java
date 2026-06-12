package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetSlotBookingRequestsBySlotUseCaseTest {

    @Test
    @DisplayName("execute: throws SLOT_NOT_FOUND if slot does not exist")
    void throwsIfSlotNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        GetSlotBookingRequestsBySlotUseCase useCase = new GetSlotBookingRequestsBySlotUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(5L, 10L));

        assertEquals(ErrorCode.SLOT_NOT_FOUND, ex.getCode());
        assertEquals(5L, ex.getDetails().get("id"));
        verify(slotRepository).findById(5L);
        verifyNoInteractions(slotBookingRequestRepository);
        verifyNoMoreInteractions(slotRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_FORBIDDEN if specialist does not own slot")
    void throwsIfSpecialistDoesNotOwnSlot() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        GetSlotBookingRequestsBySlotUseCase useCase = new GetSlotBookingRequestsBySlotUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(5L, 99L));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("specialistUserId"));
        verify(slotRepository).findById(5L);
        verifyNoInteractions(slotBookingRequestRepository);
        verifyNoMoreInteractions(slotRepository);
    }

    @Test
    @DisplayName("execute: returns slot booking requests for owner specialist")
    void returnsSlotBookingRequestsForOwnerSpecialist() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        GetSlotBookingRequestsBySlotUseCase useCase = new GetSlotBookingRequestsBySlotUseCase(
                slotBookingRequestRepository,
                slotRepository
        );
        SlotBookingRequest request = mock(SlotBookingRequest.class);
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(slotBookingRequestRepository.findBySlotId(5L)).thenReturn(List.of(request));

        List<SlotBookingRequest> result = useCase.execute(5L, 10L);

        assertEquals(List.of(request), result);
        verify(slotRepository).findById(5L);
        verify(slotBookingRequestRepository).findBySlotId(5L);
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository);
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
