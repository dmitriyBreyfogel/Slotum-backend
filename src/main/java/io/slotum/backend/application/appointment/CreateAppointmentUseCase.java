package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
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
public class CreateAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public CreateAppointmentUseCase(
            AppointmentRepository appointmentRepository,
            SpecialistRepository specialistRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public Result execute(Command command) {
        Appointment appointmentToSave = Appointment.create(
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

        if (appointmentToSave.getCustomerId() != null) {
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

        Appointment savedAppointment = appointmentRepository.save(appointmentToSave);

        return new Result(
                savedAppointment.getId(),
                savedAppointment.getStartsAt(),
                savedAppointment.getEndsAt(),
                savedAppointment.getStatus(),
                savedAppointment.getSpecialistUserId(),
                savedAppointment.getCustomerId(),
                savedAppointment.getOrganizationId()
        );
    }

    public record Command(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}

    public record Result(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}
}
