package io.slotum.backend.api.http.organization;

import io.slotum.backend.application.organization.*;
import io.slotum.backend.application.organizationMember.AddSpecialistToOrganizationUseCase;
import io.slotum.backend.application.organizationMember.GetOrganizationSpecialistsUseCase;
import io.slotum.backend.application.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
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
    private final AddSpecialistToOrganizationUseCase addSpecialistToOrganizationUseCase;
    private final RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase;

    public OrganizationController(
            CreateOrganizationUseCase createOrganizationUseCase,
            GetOrganizationUseCase getOrganizationUseCase,
            GetAllOrganizationsUseCase getAllOrganizationsUseCase,
            DeleteByIdOrganizationUseCase deleteByIdOrganizationUseCase,
            DeleteAllOrganizationUseCase deleteAllOrganizationUseCase,
            GetOrganizationSpecialistsUseCase getOrganizationSpecialistsUseCase,
            AddSpecialistToOrganizationUseCase addSpecialistToOrganizationUseCase,
            RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase
    ) {
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.getOrganizationUseCase = getOrganizationUseCase;
        this.getAllOrganizationsUseCase = getAllOrganizationsUseCase;
        this.deleteByIdOrganizationUseCase = deleteByIdOrganizationUseCase;
        this.deleteAllOrganizationUseCase = deleteAllOrganizationUseCase;
        this.getOrganizationSpecialistsUseCase = getOrganizationSpecialistsUseCase;
        this.addSpecialistToOrganizationUseCase = addSpecialistToOrganizationUseCase;
        this.removeSpecialistFromOrganizationUseCase = removeSpecialistFromOrganizationUseCase;
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> create(@RequestBody CreateOrganizationRequest request) {
        Organization result = createOrganizationUseCase.execute(
                new CreateOrganizationUseCase.Command(
                        request.name,
                        request.description
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrganizationDto(
                        result.getId(),
                        result.getName(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @PostMapping("/{organizationId}/specialists/{specialistUserId}")
    public ResponseEntity<OrganizationMemberDto> createOrganizationMember(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("specialistUserId") Long specialistUserId
    ) {
        OrganizationMember result = addSpecialistToOrganizationUseCase.execute(organizationId, specialistUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
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
    public ResponseEntity<List<SpecialistDto>> getOrganizationSpecialists(@PathVariable("organizationId") Long organizationId) {
        List<Specialist> result = getOrganizationSpecialistsUseCase.execute(organizationId);

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(specialist -> new SpecialistDto(
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

    @DeleteMapping("/{organizationId}/specialists/{specialistUserId}")
    public ResponseEntity<OrganizationMemberDto> deleteSpecialistFromOrganization(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("specialistUserId") Long specialistUserId
    ) {
        OrganizationMember result = removeSpecialistFromOrganizationUseCase.execute(organizationId, specialistUserId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
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

    public record OrganizationMemberDto(
            Long organizationId,
            Long specialistUserId
    ) {}

    public record SpecialistDto(
            Long userId,
            String description,
            Double grade
    ) {}
}
