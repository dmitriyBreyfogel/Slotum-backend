package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class GetMyAppointmentRequestsUseCaseTest {

    @Test
    @DisplayName("execute: throws USER_NOT_FOUND if customer does not exist")
    void throwsIfCustomerNotFound() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        GetMyAppointmentRequestsUseCase useCase = new GetMyAppointmentRequestsUseCase(
                appointmentRequestRepository,
                userRepository
        );
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(20L));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getCode());
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(userRepository).findById(20L);
        verifyNoInteractions(appointmentRequestRepository);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("execute: returns customer requests")
    void returnsCustomerRequests() {
        AppointmentRequestRepository appointmentRequestRepository = mock(AppointmentRequestRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        GetMyAppointmentRequestsUseCase useCase = new GetMyAppointmentRequestsUseCase(
                appointmentRequestRepository,
                userRepository
        );
        AppointmentRequest request = mock(AppointmentRequest.class);
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(appointmentRequestRepository.findByCustomerId(20L)).thenReturn(List.of(request));

        List<AppointmentRequest> result = useCase.execute(20L);

        assertEquals(List.of(request), result);
        verify(userRepository).findById(20L);
        verify(appointmentRequestRepository).findByCustomerId(20L);
        verifyNoMoreInteractions(appointmentRequestRepository, userRepository);
    }
}
