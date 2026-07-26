package io.slotum.backend.api.controllers.specialist;

import io.slotum.backend.application.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.specialist.*;
import io.slotum.backend.domain.organization.Organization;
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
    private final DeleteAllSpecialistUseCase deleteAllSpecialistUseCase;
    private final GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase;

    public SpecialistController(
            CreateSpecialistUseCase createSpecialistUseCase,
            GetSpecialistUseCase getSpecialistUseCase,
            GetAllSpecialistsUseCase getAllSpecialistsUseCase,
            DeleteByIdSpecialistUseCase deleteByIdSpecialistUseCase,
            DeleteAllSpecialistUseCase deleteAllSpecialistUseCase,
            GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.getSpecialistUseCase = getSpecialistUseCase;
        this.getAllSpecialistsUseCase = getAllSpecialistsUseCase;
        this.deleteByIdSpecialistUseCase = deleteByIdSpecialistUseCase;
        this.deleteAllSpecialistUseCase = deleteAllSpecialistUseCase;
        this.getSpecialistOrganizationsUseCase = getSpecialistOrganizationsUseCase;
    }

    @PostMapping
    public ResponseEntity<SpecialistDto> createSpecialist(@RequestBody RequestCreateSpecialist specialist) {
        Specialist result = createSpecialistUseCase.execute(
                new CreateSpecialistUseCase.Command(
                    specialist.userId,
                    specialist.description,
                    specialist.grade
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SpecialistDto(
                    result.getUserId(),
                    result.getDescription(),
                    result.getGrade()
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

    @GetMapping("/{specialistUserId}/organizations")
    public ResponseEntity<List<OrganizationDto>> getSpecialistOrganizations(
            @PathVariable("specialistUserId") Long specialistUserId
    ) {
        List<Organization> result = getSpecialistOrganizationsUseCase.execute(specialistUserId);

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(organization -> new OrganizationDto(
                        organization.getId(),
                        organization.getName(),
                        organization.getDescription(),
                        organization.getGrade()
                )).toList()
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

    @DeleteMapping()
    public ResponseEntity<Void> deleteAllSpecialists() {
        deleteAllSpecialistUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
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

    public record OrganizationDto(
            Long id,
            String name,
            String description,
            Double grade
    ) {}
}
