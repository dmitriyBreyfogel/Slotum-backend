package io.slotum.backend.api.http.organization;

import io.slotum.backend.application.organization.CreateOrganizationUseCase;
import io.slotum.backend.application.organization.GetOrganizationUseCase;
import io.slotum.backend.domain.organization.Organization;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;

    public OrganizationController(
            CreateOrganizationUseCase createOrganizationUseCase,
            GetOrganizationUseCase getOrganizationUseCase
    ) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.getOrganizationUseCase = getOrganizationUseCase;
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> create(@RequestBody CreateOrganizationRequest request) {
        CreateOrganizationUseCase.Result result = createOrganizationUseCase.execute(
                new CreateOrganizationUseCase.Command(
                        request.name,
                        request.description
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrganizationDto(
                        result.id(),
                        result.name(),
                        result.description(),
                        result.grade()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable("id") Long id) {
        Organization result = getOrganizationUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationDto(
                        result.getId(),
                        result.getName(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    public record CreateOrganizationRequest(
            String name,
            String description
    ) {}

    public record OrganizationDto(
            Long id,
            String name,
            String description,
            Double grade
    ) {}
}
