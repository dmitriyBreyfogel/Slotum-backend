package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
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

public class GetIncomingSlotBookingRequestsUseCaseTest {

    @Test
    @DisplayName("execute: throws SPECIALIST_NOT_FOUND if specialist does not exist")
    void throwsIfSpecialistNotFound() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        GetIncomingSlotBookingRequestsUseCase useCase = new GetIncomingSlotBookingRequestsUseCase(
                slotBookingRequestRepository,
                specialistRepository
        );
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(10L));

        assertEquals(ErrorCode.SPECIALIST_NOT_FOUND, ex.getCode());
        assertEquals(10L, ex.getDetails().get("specialistUserId"));
        verify(specialistRepository).findSpecialistByUserId(10L);
        verifyNoInteractions(slotBookingRequestRepository);
        verifyNoMoreInteractions(specialistRepository);
    }

    @Test
    @DisplayName("execute: returns specialist incoming requests")
    void returnsSpecialistIncomingRequests() {
        SlotBookingRequestRepository slotBookingRequestRepository = mock(SlotBookingRequestRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        GetIncomingSlotBookingRequestsUseCase useCase = new GetIncomingSlotBookingRequestsUseCase(
                slotBookingRequestRepository,
                specialistRepository
        );
        SlotBookingRequest request = mock(SlotBookingRequest.class);
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(slotBookingRequestRepository.findBySpecialistUserId(10L)).thenReturn(List.of(request));

        List<SlotBookingRequest> result = useCase.execute(10L);

        assertEquals(List.of(request), result);
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(slotBookingRequestRepository).findBySpecialistUserId(10L);
        verifyNoMoreInteractions(slotBookingRequestRepository, specialistRepository);
    }
}
