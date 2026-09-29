package io.slotum.backend.api.slot;

import io.slotum.backend.api.slot.dto.CreateSlotRequest;
import io.slotum.backend.api.slot.dto.SlotDto;
import io.slotum.backend.application.usecase.slot.CreateSlotUseCase;
import io.slotum.backend.application.usecase.slot.DeleteAllSlotUseCase;
import io.slotum.backend.application.usecase.slot.DeleteByIdSlotUseCase;
import io.slotum.backend.application.usecase.slot.GetAllSlotsUseCase;
import io.slotum.backend.application.usecase.slot.GetSlotUseCase;
import io.slotum.backend.domain.slot.Slot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class SlotController implements SlotApi {
    private final CreateSlotUseCase createSlotUseCase;
    private final GetSlotUseCase getSlotUseCase;
    private final GetAllSlotsUseCase getAllSlotsUseCase;
    private final DeleteByIdSlotUseCase deleteByIdSlotUseCase;
    private final DeleteAllSlotUseCase deleteAllSlotUseCase;

    public SlotController(
            CreateSlotUseCase createSlotUseCase,
            GetSlotUseCase getSlotUseCase,
            GetAllSlotsUseCase getAllSlotsUseCase,
            DeleteByIdSlotUseCase deleteByIdSlotUseCase,
            DeleteAllSlotUseCase deleteAllSlotUseCase
    ) {
        this.createSlotUseCase = createSlotUseCase;
        this.getSlotUseCase = getSlotUseCase;
        this.getAllSlotsUseCase = getAllSlotsUseCase;
        this.deleteByIdSlotUseCase = deleteByIdSlotUseCase;
        this.deleteAllSlotUseCase = deleteAllSlotUseCase;
    }

    @Override
    public ResponseEntity<SlotDto> create(CreateSlotRequest request) {
        Slot result = createSlotUseCase.execute(
                new CreateSlotUseCase.Command(
                        request.startsAt(),
                        request.endsAt(),
                        request.status(),
                        request.specialistUserId(),
                        request.customerId(),
                        request.organizationId()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @Override
    public ResponseEntity<SlotDto> getSlot(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getSlotUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<List<SlotDto>> getAllSlots() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllSlotsUseCase.execute().stream()
                        .map(SlotController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<SlotDto> deleteSlot(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(deleteByIdSlotUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAllSlots() {
        deleteAllSlotUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private static SlotDto toDto(Slot source) {
        return new SlotDto(
                source.getId(),
                source.getStartsAt(),
                source.getEndsAt(),
                source.getStatus(),
                source.getSpecialistUserId(),
                source.getCustomerId(),
                source.getOrganizationId()
        );
    }
}
