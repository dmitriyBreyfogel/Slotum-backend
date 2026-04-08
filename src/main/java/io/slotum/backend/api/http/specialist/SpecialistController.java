package io.slotum.backend.api.http.specialist;

import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.application.specialist.DeleteByIdSpecialistUseCase;
import io.slotum.backend.application.specialist.GetAllSpecialistsUseCase;
import io.slotum.backend.application.specialist.GetSpecialistUseCase;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {
    private final CreateSpecialistUseCase createSpecialistUseCase;
    private final GetSpecialistUseCase getSpecialistUseCase;
    private final GetAllSpecialistsUseCase getAllSpecialistsUseCase;
    private final DeleteByIdSpecialistUseCase deleteByIdSpecialistUseCase;

    public SpecialistController(
            CreateSpecialistUseCase createSpecialistUseCase,
            GetSpecialistUseCase getSpecialistUseCase,
            GetAllSpecialistsUseCase getAllSpecialistsUseCase,
            DeleteByIdSpecialistUseCase deleteByIdSpecialistUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.getSpecialistUseCase = getSpecialistUseCase;
        this.getAllSpecialistsUseCase = getAllSpecialistsUseCase;
        this.deleteByIdSpecialistUseCase = deleteByIdSpecialistUseCase;
    }

    @PostMapping
    public ResponseEntity<SpecialistDto> createSpecialist(@RequestBody RequestCreateSpecialist specialist) {
        CreateSpecialistUseCase.Result result = createSpecialistUseCase.execute(
                new CreateSpecialistUseCase.Command(
                    specialist.userId,
                    specialist.description,
                    specialist.grade
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SpecialistDto(
                    result.userId(),
                    result.description(),
                    result.grade()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialistDto> getSpecialist(@PathVariable("id") long id) {
        Specialist result = getSpecialistUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SpecialistDto(
                        result.getUserId(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @GetMapping()
    public ResponseEntity<List<SpecialistDto>> getSpecialists() {
        List<Specialist> result = getAllSpecialistsUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(specialist -> new SpecialistDto(
                        specialist.getUserId(),
                        specialist.getDescription(),
                        specialist.getGrade()
                )).toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SpecialistDto> deleteSpecialist(@PathVariable("id") Long id) {
        Specialist result = deleteByIdSpecialistUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SpecialistDto(
                        result.getUserId(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    public record RequestCreateSpecialist(
            Long userId,
            String description,
            Double grade
    ) {}

    public record SpecialistDto(
            Long userId,
            String description,
            Double grade
    ) {}
}
