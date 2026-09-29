package io.slotum.backend.application.usecase.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllSpecialistsUseCase {
    private final SpecialistRepository specialistRepository;

    public GetAllSpecialistsUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public List<Specialist> execute() {
        return specialistRepository.findAll();
    }
}
