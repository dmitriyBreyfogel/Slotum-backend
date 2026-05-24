package io.slotum.backend.application.appointmentRequest;

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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CancelAppointmentRequestUseCaseTest {

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        CancelAppointmentRequestUseCase useCase = new CancelAppointmentRequestUseCase(appointmentRequestRepository);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 20L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND, ex.getCode());
        verify(appointmentRequestRepository).findById(1L);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_FORBIDDEN if customer does not own request")
    void throwsIfCustomerDoesNotOwnRequest() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        CancelAppointmentRequestUseCase useCase = new CancelAppointmentRequestUseCase(appointmentRequestRepository);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 99L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("customerId"));
        verify(appointmentRequestRepository).findById(1L);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: cancels pending request")
    void cancelsPendingRequest() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        CancelAppointmentRequestUseCase useCase = new CancelAppointmentRequestUseCase(appointmentRequestRepository);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest()));
        when(appointmentRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentRequest result = useCase.execute(1L, 20L);

        assertEquals(AppointmentRequestStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getDecidedAt());
        verify(appointmentRequestRepository).findById(1L);
        verify(appointmentRequestRepository).save(any());
        verifyNoMoreInteractions(appointmentRequestRepository);
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
}
