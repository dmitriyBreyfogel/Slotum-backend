package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CreateSlotBookingRequestUseCaseTest {

    @Test
    @DisplayName("execute: invalid slotId is rejected before repositories")
    void rejectsInvalidSlotIdBeforeRepositories() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(0L, 20L, "message")
        ));

        assertEquals(ErrorCode.INVALID_SLOT_BOOKING_REQUEST_SLOT_ID, ex.getCode());
        verifyNoInteractions(slotBookingRequestRepository, slotRepository, userRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_NOT_FOUND if slot does not exist")
    void throwsIfSlotNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.SLOT_NOT_FOUND, ex.getCode());
        assertEquals(5L, ex.getDetails().get("id"));
        verify(slotRepository).findById(5L);
        verifyNoInteractions(userRepository, slotBookingRequestRepository);
        verifyNoMoreInteractions(slotRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_SLOT_NOT_FREE for busy slot")
    void throwsIfSlotIsNotFree() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.of(bookedSlot()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_SLOT_NOT_FREE, ex.getCode());
        assertEquals(5L, ex.getDetails().get("slotId"));
        assertEquals(SlotStatus.BOOKED, ex.getDetails().get("status"));
        verify(slotRepository).findById(5L);
        verifyNoInteractions(userRepository, slotBookingRequestRepository);
        verifyNoMoreInteractions(slotRepository);
    }

    @Test
    @DisplayName("execute: throws USER_NOT_FOUND if customer does not exist")
    void throwsIfCustomerNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(slotRepository).findById(5L);
        verify(userRepository).findById(20L);
        verifyNoInteractions(slotBookingRequestRepository);
        verifyNoMoreInteractions(slotRepository, userRepository);
    }

    @Test
    @DisplayName("execute: throws SLOT_BOOKING_REQUEST_ALREADY_EXISTS for duplicate pending request")
    void throwsIfPendingRequestAlreadyExists() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(slotBookingRequestRepository.existsPendingBySlotIdAndCustomerId(5L, 20L)).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_ALREADY_EXISTS, ex.getCode());
        assertEquals(5L, ex.getDetails().get("slotId"));
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(slotRepository).findById(5L);
        verify(userRepository).findById(20L);
        verify(slotBookingRequestRepository).existsPendingBySlotIdAndCustomerId(5L, 20L);
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository, userRepository);
    }

    @Test
    @DisplayName("execute: saves pending request and returns saved data")
    void savesPendingRequest() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SlotRepository slotRepository = mock(SlotRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateSlotBookingRequestUseCase useCase = new CreateSlotBookingRequestUseCase(
                slotBookingRequestRepository,
                slotRepository,
                userRepository
        );
        LocalDateTime savedCreatedAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        when(slotRepository.findById(5L)).thenReturn(Optional.of(freeSlot()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(slotBookingRequestRepository.existsPendingBySlotIdAndCustomerId(5L, 20L)).thenReturn(false);
        when(slotBookingRequestRepository.save(any())).thenReturn(
                SlotBookingRequest.restore(
                        1L,
                        5L,
                        20L,
                        SlotBookingRequestStatus.PENDING,
                        "message",
                        savedCreatedAt,
                        null
                )
        );

        SlotBookingRequest result = useCase.execute(
                new CreateSlotBookingRequestUseCase.Command(5L, 20L, "message")
        );

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getSlotId());
        assertEquals(20L, result.getCustomerId());
        assertEquals(SlotBookingRequestStatus.PENDING, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(savedCreatedAt, result.getCreatedAt());
        assertNull(result.getDecidedAt());

        ArgumentCaptor<SlotBookingRequest> captor = ArgumentCaptor.forClass(SlotBookingRequest.class);
        verify(slotBookingRequestRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(5L, captor.getValue().getSlotId());
        assertEquals(20L, captor.getValue().getCustomerId());
        assertEquals(SlotBookingRequestStatus.PENDING, captor.getValue().getStatus());
        assertEquals("message", captor.getValue().getMessage());
        assertNull(captor.getValue().getDecidedAt());

        verify(slotRepository).findById(5L);
        verify(userRepository).findById(20L);
        verify(slotBookingRequestRepository).existsPendingBySlotIdAndCustomerId(5L, 20L);
        verifyNoMoreInteractions(slotBookingRequestRepository, slotRepository, userRepository);
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

    private static Slot bookedSlot() {
        return Slot.restore(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.BOOKED,
                10L,
                99L,
                30L
        );
    }
}
