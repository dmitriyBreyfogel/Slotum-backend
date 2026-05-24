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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointment-requests")
public class AppointmentRequestController {
    private final CreateAppointmentRequestUseCase createAppointmentRequestUseCase;
    private final GetAppointmentRequestUseCase getAppointmentRequestUseCase;
    private final GetMyAppointmentRequestsUseCase getMyAppointmentRequestsUseCase;
    private final GetIncomingAppointmentRequestsUseCase getIncomingAppointmentRequestsUseCase;
    private final GetAppointmentRequestsByAppointmentUseCase getAppointmentRequestsByAppointmentUseCase;
    private final AcceptAppointmentRequestUseCase acceptAppointmentRequestUseCase;
    private final RejectAppointmentRequestUseCase rejectAppointmentRequestUseCase;
    private final CancelAppointmentRequestUseCase cancelAppointmentRequestUseCase;

    public AppointmentRequestController(
            CreateAppointmentRequestUseCase createAppointmentRequestUseCase,
            GetAppointmentRequestUseCase getAppointmentRequestUseCase,
            GetMyAppointmentRequestsUseCase getMyAppointmentRequestsUseCase,
            GetIncomingAppointmentRequestsUseCase getIncomingAppointmentRequestsUseCase,
            GetAppointmentRequestsByAppointmentUseCase getAppointmentRequestsByAppointmentUseCase,
            AcceptAppointmentRequestUseCase acceptAppointmentRequestUseCase,
            RejectAppointmentRequestUseCase rejectAppointmentRequestUseCase,
            CancelAppointmentRequestUseCase cancelAppointmentRequestUseCase
    ) {
        this.createAppointmentRequestUseCase = createAppointmentRequestUseCase;
        this.getAppointmentRequestUseCase = getAppointmentRequestUseCase;
        this.getMyAppointmentRequestsUseCase = getMyAppointmentRequestsUseCase;
        this.getIncomingAppointmentRequestsUseCase = getIncomingAppointmentRequestsUseCase;
        this.getAppointmentRequestsByAppointmentUseCase = getAppointmentRequestsByAppointmentUseCase;
        this.acceptAppointmentRequestUseCase = acceptAppointmentRequestUseCase;
        this.rejectAppointmentRequestUseCase = rejectAppointmentRequestUseCase;
        this.cancelAppointmentRequestUseCase = cancelAppointmentRequestUseCase;
    }

    @PostMapping
    public ResponseEntity<AppointmentRequestDto> create(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateAppointmentRequest request
    ) {
        CreateAppointmentRequestUseCase.Result result = createAppointmentRequestUseCase.execute(
                new CreateAppointmentRequestUseCase.Command(
                        request.appointmentId(),
                        currentUser.userId(),
                        request.message()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentRequestDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getAppointmentRequestUseCase.execute(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentRequestDto>> getMy(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getMyAppointmentRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(AppointmentRequestController::toDto)
                        .toList()
        );
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<AppointmentRequestDto>> getIncoming(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getIncomingAppointmentRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(AppointmentRequestController::toDto)
                        .toList()
        );
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<List<AppointmentRequestDto>> getByAppointment(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("appointmentId") Long appointmentId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAppointmentRequestsByAppointmentUseCase.execute(appointmentId, currentUser.userId()).stream()
                        .map(AppointmentRequestController::toDto)
                        .toList()
        );
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<AppointmentRequestDto> accept(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(acceptAppointmentRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<AppointmentRequestDto> reject(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(rejectAppointmentRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentRequestDto> cancel(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(cancelAppointmentRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    private static AppointmentRequestDto toDto(CreateAppointmentRequestUseCase.Result source) {
        return new AppointmentRequestDto(
                source.id(),
                source.appointmentId(),
                source.customerId(),
                source.status(),
                source.message(),
                source.createdAt(),
                source.decidedAt()
        );
    }

    private static AppointmentRequestDto toDto(AppointmentRequest source) {
        return new AppointmentRequestDto(
                source.getId(),
                source.getAppointmentId(),
                source.getCustomerId(),
                source.getStatus(),
                source.getMessage(),
                source.getCreatedAt(),
                source.getDecidedAt()
        );
    }

    public record CreateAppointmentRequest(
            Long appointmentId,
            String message
    ) {}

    public record AppointmentRequestDto(
            Long id,
            Long appointmentId,
            Long customerId,
            AppointmentRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {}
}
