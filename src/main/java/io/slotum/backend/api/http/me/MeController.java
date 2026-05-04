package io.slotum.backend.api.http.me;

import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final CreateSpecialistUseCase createSpecialistUseCase;

    public MeController(CreateSpecialistUseCase createSpecialistUseCase) {
        this.createSpecialistUseCase = createSpecialistUseCase;
    }

    @PostMapping("/specialist")
    public ResponseEntity<SpecialistDto> createSpecialist(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateSpecialistRequest request
    ) {
          CreateSpecialistUseCase.Result result = createSpecialistUseCase.execute(
                  new CreateSpecialistUseCase.Command(
                          currentUser.userId(),
                          request.description(),
                          request.grade()
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

    public record CreateSpecialistRequest(
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
