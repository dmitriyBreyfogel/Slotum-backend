package io.slotum.backend.api.http.appointment;

import io.slotum.backend.application.appointment.*;
import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Слоты", description = "Управление слотами на запись")
public class AppointmentController {
    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetAppointmentUseCase getAppointmentUseCase;
    private final GetAllAppointmentsUseCase getAllAppointmentsUseCase;
    private final DeleteByIdAppointmentUseCase deleteByIdAppointmentUseCase;
    private final DeleteAllAppointmentUseCase deleteAllAppointmentUseCase;

    public AppointmentController(
            CreateAppointmentUseCase createAppointmentUseCase,
            GetAppointmentUseCase getAppointmentUseCase,
            GetAllAppointmentsUseCase getAllAppointmentsUseCase,
            DeleteByIdAppointmentUseCase deleteByIdAppointmentUseCase,
            DeleteAllAppointmentUseCase deleteAllAppointmentUseCase
    ) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.getAppointmentUseCase = getAppointmentUseCase;
        this.getAllAppointmentsUseCase = getAllAppointmentsUseCase;
        this.deleteByIdAppointmentUseCase = deleteByIdAppointmentUseCase;
        this.deleteAllAppointmentUseCase = deleteAllAppointmentUseCase;
    }

    @Operation(
            summary = "Создать слот",
            description = "Создаёт слот по данным инициализации"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное создание слота",
                            content = @Content(
                                    schema = @Schema(implementation = AppointmentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найден один из объектов, необходимых для создания слота"
                    )
            }

    )
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

    @GetMapping()
    public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
        List<Appointment> result = getAllAppointmentsUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(appointment -> new AppointmentDto(
                        appointment.getId(),
                        appointment.getStartsAt(),
                        appointment.getEndsAt(),
                        appointment.getStatus(),
                        appointment.getSpecialistUserId(),
                        appointment.getCustomerId(),
                        appointment.getOrganizationId()
                )).toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentDto> deleteAppointment(@PathVariable("id") Long id) {
        Appointment result = deleteByIdAppointmentUseCase.execute(id);

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

    @DeleteMapping()
    public ResponseEntity<Void> deleteAllAppointments() {
        deleteAllAppointmentUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
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

