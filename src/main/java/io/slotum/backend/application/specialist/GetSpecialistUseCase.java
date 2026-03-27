package io.slotum.backend.application.specialist;

import io.slotum.backend.api.http.specialist.SpecialistController;
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

    public SpecialistController.SpecialistDto execute(Long specialistId) {
        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistId);

        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistId", specialistId)
            );
        }

        return new SpecialistController.SpecialistDto(
                specialist.get().getUserId(),
                specialist.get().getDescription(),
                specialist.get().getGrade()
        );
    }
}
