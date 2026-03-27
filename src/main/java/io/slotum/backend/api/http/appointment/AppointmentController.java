package io.slotum.backend.api.http.appointment;

import io.slotum.backend.application.appointment.CreateAppointmentUseCase;
import io.slotum.backend.application.appointment.GetAppointmentUseCase;
import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetAppointmentUseCase getAppointmentUseCase;

    public AppointmentController(
            CreateAppointmentUseCase createAppointmentUseCase,
            GetAppointmentUseCase getAppointmentUseCase
    ) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.getAppointmentUseCase = getAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody CreateAppointmentRequest request) {
        CreateAppointmentUseCase.Result result = createAppointmentUseCase.execute(
                new CreateAppointmentUseCase.Command(
                        request.startsAt,
                        request.endsAt,
                        request.status,
                        request.specialistUserId,
                        request.customerId,
                        request.organizationId
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new AppointmentDto(
                        result.id(),
                        result.startsAt(),
                        result.endsAt(),
                        result.status(),
                        result.specialistUserId(),
                        result.customerId(),
                        result.organizationId()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable("id") Long id) {
        Appointment result = getAppointmentUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new AppointmentDto(
                        result.getId(),
                        result.getStartsAt(),
                        result.getEndsAt(),
                        result.getStatus(),
                        result.getSpecialistUserId(),
                        result.getCustomerId(),
                        result.getOrganizationId()
                )
        );
    }

    public record CreateAppointmentRequest(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}

    public record AppointmentDto(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}
}

