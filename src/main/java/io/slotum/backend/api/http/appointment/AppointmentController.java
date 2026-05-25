package io.slotum.backend.api.http.appointment;

import io.slotum.backend.application.appointment.*;
import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.error.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                            responseCode = "201",
                            description = "Успешное создание слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppointmentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Не найден один из объектов, необходимых для создания слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Специалист не найден",
                                                    value = """
                                                        {
                                                            "code": "SPECIALIST_NOT_FOUND",
                                                            "message": "Specialist not found",
                                                            "details": {"specialistUserId": 1}
                                                        }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Клиент не найден",
                                                    value = """
                                                        {
                                                            "code": "USER_NOT_FOUND",
                                                            "message": "User customer not found",
                                                            "details": {"customerId": 2}
                                                        }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Организация не найдена",
                                                    value = """
                                                        {
                                                            "code": "ORGANIZATION_NOT_FOUND",
                                                            "message": "Organization not found",
                                                            "details": {"organizationId": 3}
                                                        }
                                                    """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные входные данные для создания слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Невалидный id слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ID",
                                                                "message": "Invalid appointment id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STARTS_AT",
                                                                "message": "Appointment startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ENDS_AT",
                                                                "message": "Appointment endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_TIME_RANGE",
                                                                "message": "Appointment endsAt must be after startsAt",
                                                                "details": {
                                                                    "startsAt": "2026-03-21T10:00:00",
                                                                    "endsAt": "2026-03-21T09:00:00"
                                                                }
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный статус слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STATUS",
                                                                "message": "Appointment status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_SPECIALIST_ID",
                                                                "message": "Invalid appointment specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_CUSTOMER_ID",
                                                                "message": "Invalid appointment customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ORGANIZATION_ID",
                                                                "message": "Invalid appointment organizationId",
                                                                "details": {"organizationId": -1}
                                                            }
                                                    """
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody CreateAppointmentRequest request) {
        Appointment result = createAppointmentUseCase.execute(
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

    @Operation(
            summary = "Получить слот",
            description = "Возвращает слот по идентификатору"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppointmentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Слот не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = @ExampleObject(
                                            name = "Слот не найден",
                                            value = """
                                                    {
                                                        "code": "APPOINTMENT_NOT_FOUND",
                                                        "message": "Appointment not found",
                                                        "details": {"id": 1}
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Невалидный id слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ID",
                                                                "message": "Invalid appointment id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STARTS_AT",
                                                                "message": "Appointment startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ENDS_AT",
                                                                "message": "Appointment endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_TIME_RANGE",
                                                                "message": "Appointment endsAt must be after startsAt",
                                                                "details": {
                                                                    "startsAt": "2026-03-21T10:00:00",
                                                                    "endsAt": "2026-03-21T09:00:00"
                                                                }
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный статус слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STATUS",
                                                                "message": "Appointment status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_SPECIALIST_ID",
                                                                "message": "Invalid appointment specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_CUSTOMER_ID",
                                                                "message": "Invalid appointment customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ORGANIZATION_ID",
                                                                "message": "Invalid appointment organizationId",
                                                                "details": {"organizationId": -1}
                                                            }
                                                    """
                                            )
                                    }
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Получить список слотов",
            description = "Возвращает список всех слотов"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение списка слотов",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AppointmentDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные одного из слотов",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Невалидный id слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ID",
                                                                "message": "Invalid appointment id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STARTS_AT",
                                                                "message": "Appointment startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ENDS_AT",
                                                                "message": "Appointment endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_TIME_RANGE",
                                                                "message": "Appointment endsAt must be after startsAt",
                                                                "details": {
                                                                    "startsAt": "2026-03-21T10:00:00",
                                                                    "endsAt": "2026-03-21T09:00:00"
                                                                }
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный статус слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STATUS",
                                                                "message": "Appointment status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_SPECIALIST_ID",
                                                                "message": "Invalid appointment specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_CUSTOMER_ID",
                                                                "message": "Invalid appointment customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ORGANIZATION_ID",
                                                                "message": "Invalid appointment organizationId",
                                                                "details": {"organizationId": -1}
                                                            }
                                                    """
                                            )
                                    }
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Удалить слот",
            description = "Удаляет слот по идентификатору"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное удаление слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppointmentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Слот не найден",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = @ExampleObject(
                                            name = "Слот не найден",
                                            value = """
                                                    {
                                                        "code": "APPOINTMENT_NOT_FOUND",
                                                        "message": "Appointment not found",
                                                        "details": {"id": 1}
                                                    }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные слота",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppException.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Невалидный id слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ID",
                                                                "message": "Invalid appointment id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STARTS_AT",
                                                                "message": "Appointment startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ENDS_AT",
                                                                "message": "Appointment endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_TIME_RANGE",
                                                                "message": "Appointment endsAt must be after startsAt",
                                                                "details": {
                                                                    "startsAt": "2026-03-21T10:00:00",
                                                                    "endsAt": "2026-03-21T09:00:00"
                                                                }
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный статус слота",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_STATUS",
                                                                "message": "Appointment status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_SPECIALIST_ID",
                                                                "message": "Invalid appointment specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_CUSTOMER_ID",
                                                                "message": "Invalid appointment customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_APPOINTMENT_ORGANIZATION_ID",
                                                                "message": "Invalid appointment organizationId",
                                                                "details": {"organizationId": -1}
                                                            }
                                                    """
                                            )
                                    }
                            )
                    )
            }
    )
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

    @Operation(
            summary = "Удалить все слоты",
            description = "Удаляет все слоты"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное удаление всех слотов",
                            content = @Content
                    )
            }
    )
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
