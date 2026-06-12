package io.slotum.backend.api.http.slotBookingRequest;

import io.slotum.backend.application.slotBookingRequest.AcceptSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.CancelSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.CreateSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.GetSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.GetSlotBookingRequestsBySlotUseCase;
import io.slotum.backend.application.slotBookingRequest.GetIncomingSlotBookingRequestsUseCase;
import io.slotum.backend.application.slotBookingRequest.GetMySlotBookingRequestsUseCase;
import io.slotum.backend.application.slotBookingRequest.RejectSlotBookingRequestUseCase;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SlotBookingRequestControllerTest {

    @Test
    @DisplayName("create: uses current user as customerId")
    void createUsesCurrentUserAsCustomerId() {
        CreateSlotBookingRequestUseCase createUseCase = mock(CreateSlotBookingRequestUseCase.class);
        SlotBookingRequestController controller = controller(createUseCase);
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        when(createUseCase.execute(any())).thenReturn(
                SlotBookingRequest.restore(
                        1L,
                        5L,
                        20L,
                        SlotBookingRequestStatus.PENDING,
                        "message",
                        createdAt,
                        null
                )
        );

        ResponseEntity<SlotBookingRequestController.SlotBookingRequestDto> response = controller.create(
                new AuthenticatedUser(20L, "customer@test.com"),
                new SlotBookingRequestController.CreateSlotBookingRequest(5L, "message")
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals(5L, response.getBody().slotId());
        assertEquals(20L, response.getBody().customerId());
        assertEquals(SlotBookingRequestStatus.PENDING, response.getBody().status());
        assertEquals(createdAt, response.getBody().createdAt());

        ArgumentCaptor<CreateSlotBookingRequestUseCase.Command> captor =
                ArgumentCaptor.forClass(CreateSlotBookingRequestUseCase.Command.class);
        verify(createUseCase).execute(captor.capture());
        assertEquals(5L, captor.getValue().slotId());
        assertEquals(20L, captor.getValue().customerId());
        assertEquals("message", captor.getValue().message());
        verifyNoMoreInteractions(createUseCase);
    }

    @Test
    @DisplayName("getById: returns slot booking request dto")
    void getByIdReturnsDto() {
        GetSlotBookingRequestUseCase getUseCase = mock(GetSlotBookingRequestUseCase.class);
        SlotBookingRequestController controller = controller(getUseCase);
        when(getUseCase.execute(1L)).thenReturn(request(1L));

        ResponseEntity<SlotBookingRequestController.SlotBookingRequestDto> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(getUseCase).execute(1L);
        verifyNoMoreInteractions(getUseCase);
    }

    @Test
    @DisplayName("getMy: uses current user id")
    void getMyUsesCurrentUserId() {
        GetMySlotBookingRequestsUseCase getMyUseCase = mock(GetMySlotBookingRequestsUseCase.class);
        SlotBookingRequestController controller = controller(getMyUseCase);
        when(getMyUseCase.execute(20L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<SlotBookingRequestController.SlotBookingRequestDto>> response =
                controller.getMy(new AuthenticatedUser(20L, "customer@test.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).id());
        verify(getMyUseCase).execute(20L);
        verifyNoMoreInteractions(getMyUseCase);
    }

    @Test
    @DisplayName("getIncoming: uses current user id as specialistUserId")
    void getIncomingUsesCurrentUserId() {
        GetIncomingSlotBookingRequestsUseCase getIncomingUseCase =
                mock(GetIncomingSlotBookingRequestsUseCase.class);
        SlotBookingRequestController controller = controller(getIncomingUseCase);
        when(getIncomingUseCase.execute(10L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<SlotBookingRequestController.SlotBookingRequestDto>> response =
                controller.getIncoming(new AuthenticatedUser(10L, "specialist@test.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(getIncomingUseCase).execute(10L);
        verifyNoMoreInteractions(getIncomingUseCase);
    }

    @Test
    @DisplayName("getBySlot: passes slotId and current specialist user id")
    void getBySlotPassesSlotIdAndCurrentUserId() {
        GetSlotBookingRequestsBySlotUseCase getBySlotUseCase =
                mock(GetSlotBookingRequestsBySlotUseCase.class);
        SlotBookingRequestController controller = controller(getBySlotUseCase);
        when(getBySlotUseCase.execute(5L, 10L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<SlotBookingRequestController.SlotBookingRequestDto>> response =
                controller.getBySlot(new AuthenticatedUser(10L, "specialist@test.com"), 5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(getBySlotUseCase).execute(5L, 10L);
        verifyNoMoreInteractions(getBySlotUseCase);
    }

    @Test
    @DisplayName("accept: passes request id and current specialist user id")
    void acceptPassesRequestIdAndCurrentUserId() {
        AcceptSlotBookingRequestUseCase acceptUseCase = mock(AcceptSlotBookingRequestUseCase.class);
        SlotBookingRequestController controller = controller(acceptUseCase);
        when(acceptUseCase.execute(1L, 10L)).thenReturn(request(1L));

        ResponseEntity<SlotBookingRequestController.SlotBookingRequestDto> response =
                controller.accept(new AuthenticatedUser(10L, "specialist@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(acceptUseCase).execute(1L, 10L);
        verifyNoMoreInteractions(acceptUseCase);
    }

    @Test
    @DisplayName("reject: passes request id and current specialist user id")
    void rejectPassesRequestIdAndCurrentUserId() {
        RejectSlotBookingRequestUseCase rejectUseCase = mock(RejectSlotBookingRequestUseCase.class);
        SlotBookingRequestController controller = controller(rejectUseCase);
        when(rejectUseCase.execute(1L, 10L)).thenReturn(request(1L));

        ResponseEntity<SlotBookingRequestController.SlotBookingRequestDto> response =
                controller.reject(new AuthenticatedUser(10L, "specialist@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(rejectUseCase).execute(1L, 10L);
        verifyNoMoreInteractions(rejectUseCase);
    }

    @Test
    @DisplayName("cancel: passes request id and current customer id")
    void cancelPassesRequestIdAndCurrentUserId() {
        CancelSlotBookingRequestUseCase cancelUseCase = mock(CancelSlotBookingRequestUseCase.class);
        SlotBookingRequestController controller = controller(cancelUseCase);
        when(cancelUseCase.execute(1L, 20L)).thenReturn(request(1L));

        ResponseEntity<SlotBookingRequestController.SlotBookingRequestDto> response =
                controller.cancel(new AuthenticatedUser(20L, "customer@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(cancelUseCase).execute(1L, 20L);
        verifyNoMoreInteractions(cancelUseCase);
    }

    private static SlotBookingRequestController controller(CreateSlotBookingRequestUseCase createUseCase) {
        return new SlotBookingRequestController(
                createUseCase,
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(GetSlotBookingRequestUseCase getUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                getUseCase,
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(GetMySlotBookingRequestsUseCase getMyUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                getMyUseCase,
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(GetIncomingSlotBookingRequestsUseCase getIncomingUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                getIncomingUseCase,
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(
            GetSlotBookingRequestsBySlotUseCase getBySlotUseCase
    ) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                getBySlotUseCase,
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(AcceptSlotBookingRequestUseCase acceptUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                acceptUseCase,
                mock(RejectSlotBookingRequestUseCase.class),
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(RejectSlotBookingRequestUseCase rejectUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                rejectUseCase,
                mock(CancelSlotBookingRequestUseCase.class)
        );
    }

    private static SlotBookingRequestController controller(CancelSlotBookingRequestUseCase cancelUseCase) {
        return new SlotBookingRequestController(
                mock(CreateSlotBookingRequestUseCase.class),
                mock(GetSlotBookingRequestUseCase.class),
                mock(GetMySlotBookingRequestsUseCase.class),
                mock(GetIncomingSlotBookingRequestsUseCase.class),
                mock(GetSlotBookingRequestsBySlotUseCase.class),
                mock(AcceptSlotBookingRequestUseCase.class),
                mock(RejectSlotBookingRequestUseCase.class),
                cancelUseCase
        );
    }

    private static SlotBookingRequest request(Long id) {
        return SlotBookingRequest.restore(
                id,
                5L,
                20L,
                SlotBookingRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }
}
