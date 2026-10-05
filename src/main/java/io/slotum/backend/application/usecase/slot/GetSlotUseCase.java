package io.slotum.backend.application.usecase.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetSlotUseCase {
    private final SlotRepository slotRepository;

    public GetSlotUseCase(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public Slot execute(Long id) {
        Optional<Slot> slot = slotRepository.findById(id);

        if (slot.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SLOT_NOT_FOUND,
                    "Slot not found",
                    Map.of("id", id)
            );
        }

        return slot.get();
    }
}
