package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateAppointmentUseCase {
    private final AppointmentRepository appointmentRepository;

    public CreateAppointmentUseCase(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
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

        Appointment savedAppointment = appointmentRepository.save(appointmentToSave);

        return new Result(
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
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}
}
