package io.slotum.backend.domain.specialist;

import java.util.Optional;

public interface SpecialistRepository {
    Optional<Specialist> findSpecialistByUserId(Long userId);

    Specialist save(Specialist specialist);
}
