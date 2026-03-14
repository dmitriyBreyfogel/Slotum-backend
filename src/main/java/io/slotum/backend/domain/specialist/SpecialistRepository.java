package io.slotum.backend.domain.specialist;

import io.slotum.backend.domain.user.User;

import java.util.Optional;

public interface SpecialistRepository {
    Optional<Specialist> findSpecialistByUserId(Long userId);

    Specialist save(Specialist specialist);
}
