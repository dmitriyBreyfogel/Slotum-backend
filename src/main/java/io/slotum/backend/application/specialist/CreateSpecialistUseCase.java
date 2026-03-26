package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.Optional;

public class CreateSpecialistUseCase {
    SpecialistRepository specialistRepository;

    public CreateSpecialistUseCase(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }

    public Result execute(Command command) {
        Specialist specialistToSave = Specialist.create(
                command.userId,
                command.description,
                command.grade
        );

        Optional<Specialist> existingSpecialist = specialistRepository.findSpecialistByUserId(command.userId);
        if (existingSpecialist != null && existingSpecialist.isPresent()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_ALREADY_EXISTS,
                    "Specialist with this userId already exists",
                    Map.of("userId", command.userId)
            );
        }

        Specialist savedSpecialist = specialistRepository.save(specialistToSave);
        return new Result(savedSpecialist.getUserId());
    }

    public record Command(
            Long userId,
            String description,
            Double grade
    ) {}

    public record Result(
            Long userId
    ) {}
}
