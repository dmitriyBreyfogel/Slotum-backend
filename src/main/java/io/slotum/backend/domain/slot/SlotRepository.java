package io.slotum.backend.domain.slot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository {
    Optional<Slot> findById(Long id);

    Slot save(Slot slot);

    List<Slot> findAll();

    Slot deleteById(Long id);

    void deleteAll();

    boolean existsOverlappingSlot(Long specialistUserId, LocalDateTime startsAt, LocalDateTime endsAt);

    boolean bookIfFree(Long slotId, Long specialistUserId, Long customerId);
}