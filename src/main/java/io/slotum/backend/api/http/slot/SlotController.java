package io.slotum.backend.api.http.slot;

import io.slotum.backend.application.slot.*;
import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotStatus;
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
@RequestMapping("/api/v1/slots")
@Tag(name = "Слоты", description = "Управление слотами на запись")
public class SlotController {
    private final CreateSlotUseCase createSlotUseCase;
    private final GetSlotUseCase getSlotUseCase;
    private final GetAllSlotsUseCase getAllSlotsUseCase;
    private final DeleteByIdSlotUseCase deleteByIdSlotUseCase;
    private final DeleteAllSlotUseCase deleteAllSlotUseCase;

    public SlotController(
            CreateSlotUseCase createSlotUseCase,
            GetSlotUseCase getSlotUseCase,
            GetAllSlotsUseCase getAllSlotsUseCase,
            DeleteByIdSlotUseCase deleteByIdSlotUseCase,
            DeleteAllSlotUseCase deleteAllSlotUseCase
    ) {
        this.createSlotUseCase = createSlotUseCase;
        this.getSlotUseCase = getSlotUseCase;
        this.getAllSlotsUseCase = getAllSlotsUseCase;
        this.deleteByIdSlotUseCase = deleteByIdSlotUseCase;
        this.deleteAllSlotUseCase = deleteAllSlotUseCase;
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
                                    schema = @Schema(implementation = SlotDto.class)
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
                                                                "code": "INVALID_SLOT_ID",
                                                                "message": "Invalid slot id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_STARTS_AT",
                                                                "message": "Slot startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ENDS_AT",
                                                                "message": "Slot endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_TIME_RANGE",
                                                                "message": "Slot endsAt must be after startsAt",
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
                                                                "code": "INVALID_SLOT_STATUS",
                                                                "message": "Slot status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_SPECIALIST_ID",
                                                                "message": "Invalid slot specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_CUSTOMER_ID",
                                                                "message": "Invalid slot customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ORGANIZATION_ID",
                                                                "message": "Invalid slot organizationId",
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
    public ResponseEntity<SlotDto> create(@RequestBody CreateSlotRequest request) {
        Slot result = createSlotUseCase.execute(
                new CreateSlotUseCase.Command(
                        request.startsAt,
                        request.endsAt,
                        request.status,
                        request.specialistUserId,
                        request.customerId,
                        request.organizationId
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SlotDto(
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
                                    schema = @Schema(implementation = SlotDto.class)
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
                                                        "code": "SLOT_NOT_FOUND",
                                                        "message": "Slot not found",
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
                                                                "code": "INVALID_SLOT_ID",
                                                                "message": "Invalid slot id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_STARTS_AT",
                                                                "message": "Slot startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ENDS_AT",
                                                                "message": "Slot endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_TIME_RANGE",
                                                                "message": "Slot endsAt must be after startsAt",
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
                                                                "code": "INVALID_SLOT_STATUS",
                                                                "message": "Slot status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_SPECIALIST_ID",
                                                                "message": "Invalid slot specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_CUSTOMER_ID",
                                                                "message": "Invalid slot customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ORGANIZATION_ID",
                                                                "message": "Invalid slot organizationId",
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
    public ResponseEntity<SlotDto> getSlot(@PathVariable("id") Long id) {
        Slot result = getSlotUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SlotDto(
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
                                    array = @ArraySchema(schema = @Schema(implementation = SlotDto.class))
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
                                                                "code": "INVALID_SLOT_ID",
                                                                "message": "Invalid slot id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_STARTS_AT",
                                                                "message": "Slot startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ENDS_AT",
                                                                "message": "Slot endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_TIME_RANGE",
                                                                "message": "Slot endsAt must be after startsAt",
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
                                                                "code": "INVALID_SLOT_STATUS",
                                                                "message": "Slot status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_SPECIALIST_ID",
                                                                "message": "Invalid slot specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_CUSTOMER_ID",
                                                                "message": "Invalid slot customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ORGANIZATION_ID",
                                                                "message": "Invalid slot organizationId",
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
    public ResponseEntity<List<SlotDto>> getAllSlots() {
        List<Slot> result = getAllSlotsUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(slot -> new SlotDto(
                        slot.getId(),
                        slot.getStartsAt(),
                        slot.getEndsAt(),
                        slot.getStatus(),
                        slot.getSpecialistUserId(),
                        slot.getCustomerId(),
                        slot.getOrganizationId()
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
                                    schema = @Schema(implementation = SlotDto.class)
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
                                                        "code": "SLOT_NOT_FOUND",
                                                        "message": "Slot not found",
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
                                                                "code": "INVALID_SLOT_ID",
                                                                "message": "Invalid slot id",
                                                                "details": {"id": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_STARTS_AT",
                                                                "message": "Slot startsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ENDS_AT",
                                                                "message": "Slot endsAt is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидное время начала и конца слота записи",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_TIME_RANGE",
                                                                "message": "Slot endsAt must be after startsAt",
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
                                                                "code": "INVALID_SLOT_STATUS",
                                                                "message": "Slot status is null"
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id специалиста",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_SPECIALIST_ID",
                                                                "message": "Invalid slot specialistUserId",
                                                                "details": {"specialistUserId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id пользователя",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_CUSTOMER_ID",
                                                                "message": "Invalid slot customerId",
                                                                "details": {"customerId": -1}
                                                            }
                                                    """
                                            ),
                                            @ExampleObject(
                                                    name = "Невалидный id организации",
                                                    value = """
                                                            {
                                                                "code": "INVALID_SLOT_ORGANIZATION_ID",
                                                                "message": "Invalid slot organizationId",
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
    public ResponseEntity<SlotDto> deleteSlot(@PathVariable("id") Long id) {
        Slot result = deleteByIdSlotUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SlotDto(
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
    public ResponseEntity<Void> deleteAllSlots() {
        deleteAllSlotUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public record CreateSlotRequest(
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}

    public record SlotDto(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
            Long specialistUserId,
            Long customerId,
            Long organizationId
    ) {}
}
