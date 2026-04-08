package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.specialist.SpecialistRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllSpecialistUseCase {
    private final SpecialistRepository specialistRepository;

    public DeleteAllSpecialistUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public void execute() {
        specialistRepository.deleteAll();
    }
}
