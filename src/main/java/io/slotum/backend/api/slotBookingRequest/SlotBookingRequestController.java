package io.slotum.backend.api.slotBookingRequest;

import io.slotum.backend.api.slotBookingRequest.dto.CreateSlotBookingRequest;
import io.slotum.backend.api.slotBookingRequest.dto.SlotBookingRequestDto;
import io.slotum.backend.application.slotBookingRequest.AcceptSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.CancelSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.CreateSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.GetSlotBookingRequestUseCase;
import io.slotum.backend.application.slotBookingRequest.GetSlotBookingRequestsBySlotUseCase;
import io.slotum.backend.application.slotBookingRequest.GetIncomingSlotBookingRequestsUseCase;
import io.slotum.backend.application.slotBookingRequest.GetMySlotBookingRequestsUseCase;
import io.slotum.backend.application.slotBookingRequest.RejectSlotBookingRequestUseCase;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SlotBookingRequestController implements SlotBookingRequestApi {
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

    @Override
    public ResponseEntity<SlotBookingRequestDto> create(
            AuthenticatedUser currentUser,
            CreateSlotBookingRequest request
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

    @Override
    public ResponseEntity<SlotBookingRequestDto> getById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getSlotBookingRequestUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<List<SlotBookingRequestDto>> getMy(AuthenticatedUser currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getMySlotBookingRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<List<SlotBookingRequestDto>> getIncoming(AuthenticatedUser currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getIncomingSlotBookingRequestsUseCase.execute(currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<List<SlotBookingRequestDto>> getBySlot(
            AuthenticatedUser currentUser,
            Long slotId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getSlotBookingRequestsBySlotUseCase.execute(slotId, currentUser.userId()).stream()
                        .map(SlotBookingRequestController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<SlotBookingRequestDto> accept(
            AuthenticatedUser currentUser,
            Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(acceptSlotBookingRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @Override
    public ResponseEntity<SlotBookingRequestDto> reject(
            AuthenticatedUser currentUser,
            Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                toDto(rejectSlotBookingRequestUseCase.execute(id, currentUser.userId()))
        );
    }

    @Override
    public ResponseEntity<SlotBookingRequestDto> cancel(
            AuthenticatedUser currentUser,
            Long id
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

}
