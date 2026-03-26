package io.slotum.backend.api.http.appointment;

import io.slotum.backend.application.appointment.CreateAppointmentUseCase;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final CreateAppointmentUseCase createAppointmentUseCase;

    public AppointmentController(CreateAppointmentUseCase createAppointmentUseCase) {
        this.createAppointmentUseCase = createAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateAppointmentResponse> create(@RequestBody CreateAppointmentRequest request) {
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
                new CreateAppointmentResponse(
                        result.startsAt(),
                        result.endsAt(),
                        result.status(),
                        result.specialistUserId(),
                        result.customerId(),
                        result.organizationId()
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

    public record CreateAppointmentResponse(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}
}

