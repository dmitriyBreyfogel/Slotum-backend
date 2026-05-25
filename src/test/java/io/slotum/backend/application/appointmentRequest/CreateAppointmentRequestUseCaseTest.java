package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
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

public class CreateAppointmentRequestUseCaseTest {

    @Test
    @DisplayName("execute: invalid appointmentId is rejected before repositories")
    void rejectsInvalidAppointmentIdBeforeRepositories() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateAppointmentRequestUseCase.Command(0L, 20L, "message")
        ));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_REQUEST_APPOINTMENT_ID, ex.getCode());
        verifyNoInteractions(appointmentRequestRepository, appointmentRepository, userRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_NOT_FOUND if appointment does not exist")
    void throwsIfAppointmentNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateAppointmentRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.APPOINTMENT_NOT_FOUND, ex.getCode());
        assertEquals(5L, ex.getDetails().get("id"));
        verify(appointmentRepository).findById(5L);
        verifyNoInteractions(userRepository, appointmentRequestRepository);
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE for busy appointment")
    void throwsIfAppointmentIsNotFree() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(bookedAppointment()));

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateAppointmentRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE, ex.getCode());
        assertEquals(5L, ex.getDetails().get("appointmentId"));
        assertEquals(AppointmentStatus.BOOKED, ex.getDetails().get("status"));
        verify(appointmentRepository).findById(5L);
        verifyNoInteractions(userRepository, appointmentRequestRepository);
        verifyNoMoreInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("execute: throws USER_NOT_FOUND if customer does not exist")
    void throwsIfCustomerNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateAppointmentRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(appointmentRepository).findById(5L);
        verify(userRepository).findById(20L);
        verifyNoInteractions(appointmentRequestRepository);
        verifyNoMoreInteractions(appointmentRepository, userRepository);
    }

    @Test
    @DisplayName("execute: throws APPOINTMENT_REQUEST_ALREADY_EXISTS for duplicate pending request")
    void throwsIfPendingRequestAlreadyExists() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(appointmentRequestRepository.existsPendingByAppointmentIdAndCustomerId(5L, 20L)).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(
                new CreateAppointmentRequestUseCase.Command(5L, 20L, "message")
        ));

        assertEquals(ErrorCode.APPOINTMENT_REQUEST_ALREADY_EXISTS, ex.getCode());
        assertEquals(5L, ex.getDetails().get("appointmentId"));
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(appointmentRepository).findById(5L);
        verify(userRepository).findById(20L);
        verify(appointmentRequestRepository).existsPendingByAppointmentIdAndCustomerId(5L, 20L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository, userRepository);
    }

    @Test
    @DisplayName("execute: saves pending request and returns saved data")
    void savesPendingRequest() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CreateAppointmentRequestUseCase useCase = new CreateAppointmentRequestUseCase(
                appointmentRequestRepository,
                appointmentRepository,
                userRepository
        );
        LocalDateTime savedCreatedAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(freeAppointment()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(appointmentRequestRepository.existsPendingByAppointmentIdAndCustomerId(5L, 20L)).thenReturn(false);
        when(appointmentRequestRepository.save(any())).thenReturn(
                AppointmentRequest.restore(
                        1L,
                        5L,
                        20L,
                        AppointmentRequestStatus.PENDING,
                        "message",
                        savedCreatedAt,
                        null
                )
        );

        AppointmentRequest result = useCase.execute(
                new CreateAppointmentRequestUseCase.Command(5L, 20L, "message")
        );

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getAppointmentId());
        assertEquals(20L, result.getCustomerId());
        assertEquals(AppointmentRequestStatus.PENDING, result.getStatus());
        assertEquals("message", result.getMessage());
        assertEquals(savedCreatedAt, result.getCreatedAt());
        assertNull(result.getDecidedAt());

        ArgumentCaptor<AppointmentRequest> captor = ArgumentCaptor.forClass(AppointmentRequest.class);
        verify(appointmentRequestRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(5L, captor.getValue().getAppointmentId());
        assertEquals(20L, captor.getValue().getCustomerId());
        assertEquals(AppointmentRequestStatus.PENDING, captor.getValue().getStatus());
        assertEquals("message", captor.getValue().getMessage());
        assertNull(captor.getValue().getDecidedAt());

        verify(appointmentRepository).findById(5L);
        verify(userRepository).findById(20L);
        verify(appointmentRequestRepository).existsPendingByAppointmentIdAndCustomerId(5L, 20L);
        verifyNoMoreInteractions(appointmentRequestRepository, appointmentRepository, userRepository);
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
