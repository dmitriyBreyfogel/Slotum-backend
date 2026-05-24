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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetAppointmentRequestUseCaseTest {

    @Test
    @DisplayName("execute: returns found request")
    void returnsFoundRequest() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        GetAppointmentRequestUseCase useCase = new GetAppointmentRequestUseCase(appointmentRequestRepository);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(request()));

        AppointmentRequest result = useCase.execute(1L);

        assertEquals(1L, result.getId());
        verify(appointmentRequestRepository).findById(1L);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        GetAppointmentRequestUseCase useCase = new GetAppointmentRequestUseCase(appointmentRequestRepository);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(appointmentRequestRepository).findById(1L);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    private static AppointmentRequest request() {
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
