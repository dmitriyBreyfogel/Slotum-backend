package io.slotum.backend.api.http.organization;

import io.slotum.backend.api.http.specialist.SpecialistController;
import io.slotum.backend.application.organization.*;
import io.slotum.backend.application.organizationMember.GetOrganizationSpecialistsUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final GetOrganizationUseCase getOrganizationUseCase;
    private final GetAllOrganizationsUseCase getAllOrganizationsUseCase;
    private final DeleteByIdOrganizationUseCase deleteByIdOrganizationUseCase;
    private final DeleteAllOrganizationUseCase deleteAllOrganizationUseCase;
    private final GetOrganizationSpecialistsUseCase getOrganizationSpecialistsUseCase;

    public OrganizationController(
            CreateOrganizationUseCase createOrganizationUseCase,
            GetOrganizationUseCase getOrganizationUseCase,
            GetAllOrganizationsUseCase getAllOrganizationsUseCase,
            DeleteByIdOrganizationUseCase deleteByIdOrganizationUseCase,
            DeleteAllOrganizationUseCase deleteAllOrganizationUseCase,
            GetOrganizationSpecialistsUseCase getOrganizationSpecialistsUseCase
    ) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.getAllOrganizationsUseCase = getAllOrganizationsUseCase;
        this.deleteByIdOrganizationUseCase = deleteByIdOrganizationUseCase;
        this.deleteAllOrganizationUseCase = deleteAllOrganizationUseCase;
        this.getOrganizationSpecialistsUseCase = getOrganizationSpecialistsUseCase;
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

    @GetMapping()
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        List<Organization> result = getAllOrganizationsUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(organization -> new OrganizationDto(
                        organization.getId(),
                        organization.getName(),
                        organization.getDescription(),
                        organization.getGrade()
                )).toList()
        );
    }

    @GetMapping("/{organizationId}/specialists")
    public ResponseEntity<List<SpecialistController.SpecialistDto>> getOrganizationSpecialists(@PathVariable("organizationId") Long organizationId) {
        List<Specialist> result = getOrganizationSpecialistsUseCase.execute(organizationId);

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(specialist -> new SpecialistController.SpecialistDto(
                        specialist.getUserId(),
                        specialist.getDescription(),
                        specialist.getGrade()
                )).toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrganizationDto> deleteOrganization(@PathVariable("id") Long id) {
        Organization result = deleteByIdOrganizationUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationDto(
                        result.getId(),
                        result.getName(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteAllOrganizations() {
        deleteAllOrganizationUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
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
