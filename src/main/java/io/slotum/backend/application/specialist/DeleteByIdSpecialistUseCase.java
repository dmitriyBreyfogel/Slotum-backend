package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class DeleteByIdSpecialistUseCase {
    private final SpecialistRepository specialistRepository;

    public DeleteByIdSpecialistUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public Specialist execute(Long id) {
        Optional<Specialist> specialist = specialistRepository.findById(id);

        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("id", id)
            );
        }

        return specialistRepository.deleteById(id);
    }
}
