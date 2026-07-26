package io.slotum.backend.api.controllers.slotBookingRequest;

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
@RequestMapping("/api/v1/slot-booking-requests")
public class SlotBookingRequestController {
    private final CreateSlotBookingRequestUseCase createSlotBookingRequestUseCase;
    private final GetSlotBookingRequestUseCase getSlotBookingRequestUseCase;
    private final GetMySlotBookingRequestsUseCase getMySlotBookingRequestsUseCase;
    private final GetIncomingSlotBookingRequestsUseCase getIncomingSlotBookingRequestsUseCase;
    private final GetSlotBookingRequestsBySlotUseCase getSlotBookingRequestsBySlotUseCase;
    private final AcceptSlotBookingRequestUseCase acceptSlotBookingRequestUseCase;
    private final RejectSlotBookingRequestUseCase rejectSlotBookingRequestUseCase;
    private final CancelSlotBookingRequestUseCase cancelSlotBookingRequestUseCase;

    public SlotBookingRequestController(
            CreateSlotBookingRequestUseCase createSlotBookingRequestUseCase,
            GetSlotBookingRequestUseCase getSlotBookingRequestUseCase,
            GetMySlotBookingRequestsUseCase getMySlotBookingRequestsUseCase,
            GetIncomingSlotBookingRequestsUseCase getIncomingSlotBookingRequestsUseCase,
            GetSlotBookingRequestsBySlotUseCase getSlotBookingRequestsBySlotUseCase,
            AcceptSlotBookingRequestUseCase acceptSlotBookingRequestUseCase,
            RejectSlotBookingRequestUseCase rejectSlotBookingRequestUseCase,
            CancelSlotBookingRequestUseCase cancelSlotBookingRequestUseCase
    ) {
        this.createSlotBookingRequestUseCase = createSlotBookingRequestUseCase;
        this.getSlotBookingRequestUseCase = getSlotBookingRequestUseCase;
        this.getMySlotBookingRequestsUseCase = getMySlotBookingRequestsUseCase;
        this.getIncomingSlotBookingRequestsUseCase = getIncomingSlotBookingRequestsUseCase;
        this.getSlotBookingRequestsBySlotUseCase = getSlotBookingRequestsBySlotUseCase;
        this.acceptSlotBookingRequestUseCase = acceptSlotBookingRequestUseCase;
        this.rejectSlotBookingRequestUseCase = rejectSlotBookingRequestUseCase;
        this.cancelSlotBookingRequestUseCase = cancelSlotBookingRequestUseCase;
    }

    @PostMapping
    public ResponseEntity<SlotBookingRequestDto> create(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateSlotBookingRequest request
    ) {
        SlotBookingRequest result = createSlotBookingRequestUseCase.execute(
                new CreateSlotBookingRequestUseCase.Command(
                        request.slotId(),
                        currentUser.userId(),
                        request.message()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlotBookingRequestDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getSlotBookingRequestUseCase.execute(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SlotBookingRequestDto>> getMy(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getMySlotBookingRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<SlotBookingRequestDto>> getIncoming(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getIncomingSlotBookingRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @GetMapping("/slots/{slotId}")
    public ResponseEntity<List<SlotBookingRequestDto>> getBySlot(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("slotId") Long slotId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getSlotBookingRequestsBySlotUseCase.execute(slotId, currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<SlotBookingRequestDto> accept(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(acceptSlotBookingRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<SlotBookingRequestDto> reject(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(rejectSlotBookingRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SlotBookingRequestDto> cancel(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(cancelSlotBookingRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    private static SlotBookingRequestDto toDto(SlotBookingRequest source) {
        return new SlotBookingRequestDto(
                source.getId(),
                source.getSlotId(),
                source.getCustomerId(),
                source.getStatus(),
                source.getMessage(),
                source.getCreatedAt(),
                source.getDecidedAt()
        );
    }

    public record CreateSlotBookingRequest(
            Long slotId,
            String message
    ) {}

    public record SlotBookingRequestDto(
            Long id,
            Long slotId,
            Long customerId,
            SlotBookingRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {}
}
