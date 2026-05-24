package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
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

public class AcceptAppointmentRequestUseCaseTest {

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_NOT_FOUND if request does not exist")
    void throwsIfRequestNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_NOT_FOUND, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(appointmentRequestRepository).findById(1L);
        verifyNoInteractions(appointmentRepository);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_NOT_PENDING if request already finished")
    void throwsIfRequestIsNotPending() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(rejectedRequest()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_NOT_PENDING, ex.getCode());
        assertEquals(1L, ex.getDetails().get("id"));
        verify(appointmentRequestRepository).findById(1L);
        verifyNoInteractions(appointmentRepository);
        verifyNoMoreInteractions(appointmentRequestRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_NOT_FOUND if appointment does not exist")
    void throwsIfAppointmentNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest(1L, 20L)));
        when(appointmentRepository.findById(5L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_NOT_FOUND, ex.getCode());
        assertEquals(5L, ex.getDetails().get("id"));
        verify(appointmentRequestRepository).findById(1L);
        verify(appointmentRepository).findById(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_FORBIDDEN if specialist does not own appointment")
    void throwsIfSpecialistDoesNotOwnAppointment() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest(1L, 20L)));
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 99L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_FORBIDDEN, ex.getCode());
        assertEquals(5L, ex.getDetails().get("appointmentId"));
        assertEquals(99L, ex.getDetails().get("specialistUserId"));
        verify(appointmentRequestRepository).findById(1L);
        verify(appointmentRepository).findById(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE if appointment is busy")
    void throwsIfAppointmentIsNotFree() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest(1L, 20L)));
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(bookedAppointment()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(1L, 10L));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE, ex.getCode());
        assertEquals(5L, ex.getDetails().get("appointmentId"));
        assertEquals(AppointmentStatus.BOOKED, ex.getDetails().get("status"));
        verify(appointmentRequestRepository).findById(1L);
        verify(appointmentRepository).findById(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository);
    }

    @Test
    @DisplayName("execute: books appointment, accepts request and rejects other pending requests")
    void acceptsRequestBooksAppointmentAndRejectsOthers() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        AcceptAppointmentRequestUseCase useCase = new AcceptAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository
        );
        AppointmentRequest targetRequest = pendingRequest(1L, 20L);
        AppointmentRequest otherRequest = pendingRequest(2L, 21L);
        when(appointmentRequestRepository.findById(1L)).thenReturn(Optional.of(targetRequest));
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRequestRepository.findPendingByAppointmentId(5L)).thenReturn(List.of(targetRequest, otherRequest));

        AppointmentRequest result = useCase.execute(1L, 10L);

        assertEquals(AppointmentRequestStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getDecidedAt());

        ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());
        assertEquals(AppointmentStatus.BOOKED, appointmentCaptor.getValue().getStatus());
        assertEquals(20L, appointmentCaptor.getValue().getCustomerId());

        ArgumentCaptor<AppointmentRequest> requestCaptor = ArgumentCaptor.forClass(AppointmentRequest.class);
        verify(appointmentRequestRepository, org.mockito.Mockito.times(2)).save(requestCaptor.capture());
        List<AppointmentRequest> savedRequests = requestCaptor.getAllValues();
        assertEquals(AppointmentRequestStatus.ACCEPTED, savedRequests.get(0).getStatus());
        assertEquals(AppointmentRequestStatus.REJECTED, savedRequests.get(1).getStatus());
        assertEquals(2L, savedRequests.get(1).getId());

        verify(appointmentRequestRepository).findById(1L);
        verify(appointmentRepository).findById(5L);
        verify(appointmentRequestRepository).findPendingByAppointmentId(5L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository);
    }

    private static AppointmentRequest pendingRequest(Long id, Long customerId) {
        return AppointmentRequest.restore(
                id,
                5L,
                customerId,
                AppointmentRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }

    private static AppointmentRequest rejectedRequest() {
        return AppointmentRequest.restore(
                1L,
                5L,
                20L,
                AppointmentRequestStatus.REJECTED,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                LocalDateTime.of(2026, 5, 24, 11, 0)
        );
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

    private static Appointment bookedAppointment() {
        return Appointment.restore(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                AppointmentStatus.BOOKED,
                10L,
                99L,
                30L
        );
    }
}
