package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
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

public class GetAppointmentRequestsByAppointmentUseCaseTest {

    @Test
    @DisplayName("execute: throws APPOINTMENT_NOT_FOUND if appointment does not exist")
    void throwsIfAppointmentNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        GetAppointmentRequestsByAppointmentUseCase useCase = new GetAppointmentRequestsByAppointmentUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(5L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_NOT_FOUND, ex.getCode());
        assertEquals(5L, ex.getDetails().get("id"));
        verify(appointmentRepository).findById(5L);
        verifyNoInteractions(appointmentRequestRepository);
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_FORBIDDEN if specialist does not own appointment")
    void throwsIfSpecialistDoesNotOwnAppointment() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        GetAppointmentRequestsByAppointmentUseCase useCase = new GetAppointmentRequestsByAppointmentUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(5L, 99L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(99L, ex.getDetails().get("specialistUserId"));
        verify(appointmentRepository).findById(5L);
        verifyNoInteractions(appointmentRequestRepository);
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("execute: returns appointment requests for owner specialist")
    void returnsAppointmentRequestsForOwnerSpecialist() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        GetAppointmentRequestsByAppointmentUseCase useCase = new GetAppointmentRequestsByAppointmentUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        AppointmentRequest request = mock(AppointmentRequest.class);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));
        when(appointmentRequestRepository.findByAppointmentId(5L)).thenReturn(List.of(request));

        List<AppointmentRequest> result = useCase.execute(5L, 10L);

        assertEquals(List.of(request), result);
        verify(appointmentRepository).findById(5L);
        verify(appointmentRequestRepository).findByAppointmentId(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository);
    }

    private static Appointment freeAppointment() {
        return Appointment.restore(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                AppointmentStatus.FREE,
                10L,
                null,
                30L
        );
    }
}
