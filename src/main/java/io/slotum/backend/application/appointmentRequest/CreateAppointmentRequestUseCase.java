package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CreateAppointmentRequestUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public CreateAppointmentRequestUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            AppointmentRepository appointmentRepository,
            UserRepository userRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public AppointmentRequest execute(Command command) {
        AppointmentRequest appointmentRequestToSave = AppointmentRequest.create(
                command.appointmentId,
                command.customerId,
                command.message,
                LocalDateTime.now()
        );

        Optional<Appointment> appointment = appointmentRepository.findById(command.appointmentId);
        if (appointment.isEmpty()) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_NOT_FOUND,
                    "Appointment not found",
                    Map.of("id", command.appointmentId)
            );
        }

        if (appointment.get().getStatus() != AppointmentStatus.FREE) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_APPOINTMENT_NOT_FREE,
                    "Appointment is not free",
                    Map.of(
                            "appointmentId", command.appointmentId,
                            "status", appointment.get().getStatus()
                    )
            );
        }

        Optional<User> customer = userRepository.findById(command.customerId);
        if (customer.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User customer not found",
                    Map.of("customerId", command.customerId)
            );
        }

        if (appointmentRequestRepository.existsPendingByAppointmentIdAndCustomerId(
                command.appointmentId,
                command.customerId
        )) {
            throw AppException.build(
                    ErrorCode.APPOINTMENT_REQUEST_ALREADY_EXISTS,
                    "Pending appointment request already exists",
                    Map.of(
                            "appointmentId", command.appointmentId,
                            "customerId", command.customerId
                    )
            );
        }

        return appointmentRequestRepository.save(appointmentRequestToSave);
    }

    public record Command(
            Long appointmentId,
            Long customerId,
            String message
    ) {}

}
