package io.slotum.backend.api.http.specialist;

import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {
    private final CreateSpecialistUseCase createSpecialistUseCase;

    public SpecialistController(CreateSpecialistUseCase createSpecialistUseCase) {
        this.createSpecialistUseCase = createSpecialistUseCase;
    }

    @PostMapping
    public ResponseEntity<ResponseCreateSpecialist> createSpecialist(@RequestBody RequestCreateSpecialist specialist) {
        CreateSpecialistUseCase.Result result = createSpecialistUseCase.execute(
                new CreateSpecialistUseCase.Command(
                    specialist.userId,
                    specialist.description,
                    specialist.grade
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseCreateSpecialist(
                    result.userId(),
                    result.description(),
                    result.grade()
                )
        );
    }

    public record RequestCreateSpecialist(
            Long userId,
            String description,
            Double grade
    ) {}

    public record ResponseCreateSpecialist(
            Long userId,
            String description,
            Double grade
    ) {}
}
