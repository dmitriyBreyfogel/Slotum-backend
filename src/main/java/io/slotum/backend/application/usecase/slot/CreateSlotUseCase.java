package io.slotum.backend.application.usecase.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CreateSlotUseCase {
    private final SlotRepository slotRepository;
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public CreateSlotUseCase(
            SlotRepository slotRepository,
            SpecialistRepository specialistRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository
    ) {
        this.slotRepository = slotRepository;
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public Slot execute(Command command) {
        Slot slotToSave = Slot.create(
                command.startsAt,
                command.endsAt,
                command.status,
                command.specialistUserId,
                command.customerId,
                command.organizationId
        );

        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(command.specialistUserId);
        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", command.specialistUserId)
            );
        }

        if (slotToSave.getCustomerId() != null) {
            Optional<User> user = userRepository.findById(command.customerId);
            if (user.isEmpty()) {
                throw AppException.build(
                        ErrorCode.USER_NOT_FOUND,
                        "User customer not found",
                        Map.of("customerId", command.customerId)
                );
            }
        }

        Optional<Organization> organization = organizationRepository.findById(command.organizationId);
        if (organization.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ORGANIZATION_NOT_FOUND,
                    "Organization not found",
                    Map.of("organizationId", command.organizationId)
            );
        }

        if (slotRepository.existsOverlappingSlot(command.specialistUserId, command.startsAt, command.endsAt)) {
            throw AppException.build(
                ErrorCode.SLOT_OVERLAPPING,
                    "Slot overlapping",
                    Map.of("startsAt", command.startsAt,
                            "endsAt", command.endsAt)
            );
        }

        return slotRepository.save(slotToSave);
    }

    public record Command(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}

}
