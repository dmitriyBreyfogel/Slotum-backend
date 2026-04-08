package io.slotum.backend.domain.specialist;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository {
    Optional<Specialist> findById(Long id);

    Optional<Specialist> findSpecialistByUserId(Long userId);

    Specialist save(Specialist specialist);

    List<Specialist> findAll();

    Specialist deleteById(Long id);

    void deleteAll();
}
