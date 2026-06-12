package io.slotum.backend.application.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllSlotsUseCase {
    private final SlotRepository slotRepository;

    public GetAllSlotsUseCase(SlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public List<Slot> execute() {
        return slotRepository.findAll();
    }
}
