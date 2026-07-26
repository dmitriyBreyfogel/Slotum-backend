package io.slotum.backend.application.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeleteByIdSlotUseCase {
    private final SlotRepository slotRepository;

    public DeleteByIdSlotUseCase(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public Slot execute(Long id) {
        return slotRepository.deleteById(id).orElseThrow(() -> AppException.build(
                ErrorCode.SLOT_NOT_FOUND,
                "Slot not found",
                Map.of("id", id)
        ));
    }
}
