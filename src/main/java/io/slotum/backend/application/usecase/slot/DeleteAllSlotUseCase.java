package io.slotum.backend.application.usecase.slot;

import io.slotum.backend.domain.slot.SlotRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllSlotUseCase {
    private final SlotRepository slotRepository;

    public DeleteAllSlotUseCase(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public void execute() {
        slotRepository.deleteAll();
    }
}
