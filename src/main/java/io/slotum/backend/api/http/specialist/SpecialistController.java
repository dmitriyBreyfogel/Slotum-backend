package io.slotum.backend.api.http.specialist;

import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.application.specialist.GetSpecialistUseCase;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {
    private final CreateSpecialistUseCase createSpecialistUseCase;
    private final GetSpecialistUseCase getSpecialistUseCase;

    public SpecialistController(
            CreateSpecialistUseCase createSpecialistUseCase,
            GetSpecialistUseCase getSpecialistUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.getSpecialistUseCase = getSpecialistUseCase;
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
        SpecialistDto result = getSpecialistUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SpecialistDto(
                        result.userId,
                        result.description,
                        result.grade
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
