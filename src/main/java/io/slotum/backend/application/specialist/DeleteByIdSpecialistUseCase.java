package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeleteByIdSpecialistUseCase {
    private final SpecialistRepository specialistRepository;

    public DeleteByIdSpecialistUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public Specialist execute(Long id) {
        return specialistRepository.deleteById(id).orElseThrow(() -> AppException.build(
                ErrorCode.SPECIALIST_NOT_FOUND,
                "Specialist not found",
                Map.of("id", id)
        ));
    }
}
