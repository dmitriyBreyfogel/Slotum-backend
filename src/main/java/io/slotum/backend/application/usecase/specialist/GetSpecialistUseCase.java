package io.slotum.backend.application.usecase.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetSpecialistUseCase {
    private final SpecialistRepository specialistRepository;

    public GetSpecialistUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public Specialist execute(Long specialistId) {
        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistId);

        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", specialistId)
            );
        }

        return specialist.get();
    }
}
