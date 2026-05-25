package io.slotum.backend.application.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CreateSpecialistUseCase {
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;

    public CreateSpecialistUseCase(SpecialistRepository specialistRepository, UserRepository userRepository) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
    }

    public Specialist execute(Command command) {
        Specialist specialistToSave = Specialist.create(
                command.userId,
                command.description,
                command.grade
        );

        Optional<User> existingUser = userRepository.findById(command.userId);
        if (existingUser.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User not found",
                    Map.of("userId", command.userId)
            );
        }

        Optional<Specialist> existingSpecialist = specialistRepository.findSpecialistByUserId(command.userId);
        if (existingSpecialist != null && existingSpecialist.isPresent()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_ALREADY_EXISTS,
                    "Specialist with this userId already exists",
                    Map.of("userId", command.userId)
            );
        }

        return specialistRepository.save(specialistToSave);
    }

    public record Command(
            Long userId,
            String description,
            Double grade
    ) {}

}
