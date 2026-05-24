package io.slotum.backend.api.http.appointmentRequest;

import io.slotum.backend.application.appointmentRequest.AcceptAppointmentRequestUseCase;
import io.slotum.backend.application.appointmentRequest.CancelAppointmentRequestUseCase;
import io.slotum.backend.application.appointmentRequest.CreateAppointmentRequestUseCase;
import io.slotum.backend.application.appointmentRequest.GetAppointmentRequestUseCase;
import io.slotum.backend.application.appointmentRequest.GetAppointmentRequestsByAppointmentUseCase;
import io.slotum.backend.application.appointmentRequest.GetIncomingAppointmentRequestsUseCase;
import io.slotum.backend.application.appointmentRequest.GetMyAppointmentRequestsUseCase;
import io.slotum.backend.application.appointmentRequest.RejectAppointmentRequestUseCase;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
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

public class AppointmentRequestControllerTest {

    @Test
    @DisplayName("create: uses current user as customerId")
    void createUsesCurrentUserAsCustomerId() {
        CreateAppointmentRequestUseCase createUseCase = mock(CreateAppointmentRequestUseCase.class);
        AppointmentRequestController controller = controller(createUseCase);
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        when(createUseCase.execute(any())).thenReturn(
                new CreateAppointmentRequestUseCase.Result(
                        1L,
                        5L,
                        20L,
                        AppointmentRequestStatus.PENDING,
                        "message",
                        createdAt,
                        null
                )
        );

        ResponseEntity<AppointmentRequestController.AppointmentRequestDto> response = controller.create(
                new AuthenticatedUser(20L, "customer@test.com"),
                new AppointmentRequestController.CreateAppointmentRequest(5L, "message")
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        assertEquals(5L, response.getBody().appointmentId());
        assertEquals(20L, response.getBody().customerId());
        assertEquals(AppointmentRequestStatus.PENDING, response.getBody().status());
        assertEquals(createdAt, response.getBody().createdAt());

        ArgumentCaptor<CreateAppointmentRequestUseCase.Command> captor =
                ArgumentCaptor.forClass(CreateAppointmentRequestUseCase.Command.class);
        verify(createUseCase).execute(captor.capture());
        assertEquals(5L, captor.getValue().appointmentId());
        assertEquals(20L, captor.getValue().customerId());
        assertEquals("message", captor.getValue().message());
        verifyNoMoreInteractions(createUseCase);
    }

    @Test
    @DisplayName("getById: returns appointment request dto")
    void getByIdReturnsDto() {
        GetAppointmentRequestUseCase getUseCase = mock(GetAppointmentRequestUseCase.class);
        AppointmentRequestController controller = controller(getUseCase);
        when(getUseCase.execute(1L)).thenReturn(request(1L));

        ResponseEntity<AppointmentRequestController.AppointmentRequestDto> response = controller.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(getUseCase).execute(1L);
        verifyNoMoreInteractions(getUseCase);
    }

    @Test
    @DisplayName("getMy: uses current user id")
    void getMyUsesCurrentUserId() {
        GetMyAppointmentRequestsUseCase getMyUseCase = mock(GetMyAppointmentRequestsUseCase.class);
        AppointmentRequestController controller = controller(getMyUseCase);
        when(getMyUseCase.execute(20L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<AppointmentRequestController.AppointmentRequestDto>> response =
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
        GetIncomingAppointmentRequestsUseCase getIncomingUseCase =
                mock(GetIncomingAppointmentRequestsUseCase.class);
        AppointmentRequestController controller = controller(getIncomingUseCase);
        when(getIncomingUseCase.execute(10L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<AppointmentRequestController.AppointmentRequestDto>> response =
                controller.getIncoming(new AuthenticatedUser(10L, "specialist@test.com"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(getIncomingUseCase).execute(10L);
        verifyNoMoreInteractions(getIncomingUseCase);
    }

    @Test
    @DisplayName("getByAppointment: passes appointmentId and current specialist user id")
    void getByAppointmentPassesAppointmentIdAndCurrentUserId() {
        GetAppointmentRequestsByAppointmentUseCase getByAppointmentUseCase =
                mock(GetAppointmentRequestsByAppointmentUseCase.class);
        AppointmentRequestController controller = controller(getByAppointmentUseCase);
        when(getByAppointmentUseCase.execute(5L, 10L)).thenReturn(List.of(request(1L)));

        ResponseEntity<List<AppointmentRequestController.AppointmentRequestDto>> response =
                controller.getByAppointment(new AuthenticatedUser(10L, "specialist@test.com"), 5L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(getByAppointmentUseCase).execute(5L, 10L);
        verifyNoMoreInteractions(getByAppointmentUseCase);
    }

    @Test
    @DisplayName("accept: passes request id and current specialist user id")
    void acceptPassesRequestIdAndCurrentUserId() {
        AcceptAppointmentRequestUseCase acceptUseCase = mock(AcceptAppointmentRequestUseCase.class);
        AppointmentRequestController controller = controller(acceptUseCase);
        when(acceptUseCase.execute(1L, 10L)).thenReturn(request(1L));

        ResponseEntity<AppointmentRequestController.AppointmentRequestDto> response =
                controller.accept(new AuthenticatedUser(10L, "specialist@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(acceptUseCase).execute(1L, 10L);
        verifyNoMoreInteractions(acceptUseCase);
    }

    @Test
    @DisplayName("reject: passes request id and current specialist user id")
    void rejectPassesRequestIdAndCurrentUserId() {
        RejectAppointmentRequestUseCase rejectUseCase = mock(RejectAppointmentRequestUseCase.class);
        AppointmentRequestController controller = controller(rejectUseCase);
        when(rejectUseCase.execute(1L, 10L)).thenReturn(request(1L));

        ResponseEntity<AppointmentRequestController.AppointmentRequestDto> response =
                controller.reject(new AuthenticatedUser(10L, "specialist@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(rejectUseCase).execute(1L, 10L);
        verifyNoMoreInteractions(rejectUseCase);
    }

    @Test
    @DisplayName("cancel: passes request id and current customer id")
    void cancelPassesRequestIdAndCurrentUserId() {
        CancelAppointmentRequestUseCase cancelUseCase = mock(CancelAppointmentRequestUseCase.class);
        AppointmentRequestController controller = controller(cancelUseCase);
        when(cancelUseCase.execute(1L, 20L)).thenReturn(request(1L));

        ResponseEntity<AppointmentRequestController.AppointmentRequestDto> response =
                controller.cancel(new AuthenticatedUser(20L, "customer@test.com"), 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().id());
        verify(cancelUseCase).execute(1L, 20L);
        verifyNoMoreInteractions(cancelUseCase);
    }

    private static AppointmentRequestController controller(CreateAppointmentRequestUseCase createUseCase) {
        return new AppointmentRequestController(
                createUseCase,
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(GetAppointmentRequestUseCase getUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                getUseCase,
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(GetMyAppointmentRequestsUseCase getMyUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                getMyUseCase,
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(GetIncomingAppointmentRequestsUseCase getIncomingUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                getIncomingUseCase,
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(
            GetAppointmentRequestsByAppointmentUseCase getByAppointmentUseCase
    ) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                getByAppointmentUseCase,
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(AcceptAppointmentRequestUseCase acceptUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                acceptUseCase,
                mock(RejectAppointmentRequestUseCase.class),
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(RejectAppointmentRequestUseCase rejectUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                rejectUseCase,
                mock(CancelAppointmentRequestUseCase.class)
        );
    }

    private static AppointmentRequestController controller(CancelAppointmentRequestUseCase cancelUseCase) {
        return new AppointmentRequestController(
                mock(CreateAppointmentRequestUseCase.class),
                mock(GetAppointmentRequestUseCase.class),
                mock(GetMyAppointmentRequestsUseCase.class),
                mock(GetIncomingAppointmentRequestsUseCase.class),
                mock(GetAppointmentRequestsByAppointmentUseCase.class),
                mock(AcceptAppointmentRequestUseCase.class),
                mock(RejectAppointmentRequestUseCase.class),
                cancelUseCase
        );
    }

    private static AppointmentRequest request(Long id) {
        return AppointmentRequest.restore(
                id,
                5L,
                20L,
                AppointmentRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }
}
