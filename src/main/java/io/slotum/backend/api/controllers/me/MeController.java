package io.slotum.backend.api.controllers.me;

import io.slotum.backend.application.organization.CreateMyOrganizationUseCase;
import io.slotum.backend.application.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final CreateSpecialistUseCase createSpecialistUseCase;
    private final CreateMyOrganizationUseCase createMyOrganizationUseCase;
    private final GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase;
    private final RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase;

    public MeController(
            CreateSpecialistUseCase createSpecialistUseCase,
            CreateMyOrganizationUseCase createMyOrganizationUseCase,
            GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase,
            RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.createMyOrganizationUseCase = createMyOrganizationUseCase;
        this.getSpecialistOrganizationsUseCase = getSpecialistOrganizationsUseCase;
        this.removeSpecialistFromOrganizationUseCase = removeSpecialistFromOrganizationUseCase;
    }

    @PostMapping("/specialist")
    public ResponseEntity<SpecialistDto> createSpecialistFromMe(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateSpecialistRequest request
    ) {
          Specialist result = createSpecialistUseCase.execute(
                  new CreateSpecialistUseCase.Command(
                          currentUser.userId(),
                          request.description(),
                          request.grade()
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

    @PostMapping("/organizations")
    public ResponseEntity<OrganizationDto> createMyOrganization(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestBody CreateOrganizationRequest request
    ) {
        Organization result = createMyOrganizationUseCase.execute(
                new CreateMyOrganizationUseCase.Command(
                        currentUser.userId(),
                        request.name(),
                        request.description()
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

    @GetMapping("/organizations")
    public ResponseEntity<List<OrganizationDto>> getMyOrganizations(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        List<Organization> result = getSpecialistOrganizationsUseCase.execute(currentUser.userId());

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(organization -> new OrganizationDto(
                        organization.getId(),
                        organization.getName(),
                        organization.getDescription(),
                        organization.getGrade()
                )).toList()
        );
    }

    @DeleteMapping("/organizations/{organizationId}")
    public ResponseEntity<OrganizationMemberDto> removeMeFromOrganization(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("organizationId") Long organizationId
    ) {
        OrganizationMember result = removeSpecialistFromOrganizationUseCase.execute(organizationId, currentUser.userId());

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
                )
        );
    }

    public record CreateSpecialistRequest(
            String description,
            Double grade
    ) {}

    public record CreateOrganizationRequest(
            String name,
            String description
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

    public record OrganizationMemberDto(
            Long organizationId,
            Long specialistUserId
    ) {}
}
