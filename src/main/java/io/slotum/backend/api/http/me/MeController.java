package io.slotum.backend.api.http.me;

import io.slotum.backend.application.organizationMember.AddSpecialistToOrganizationUseCase;
import io.slotum.backend.application.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationMember;
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
    private final GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase;
    private final AddSpecialistToOrganizationUseCase addSpecialistToOrganizationUseCase;

    public MeController(
            CreateSpecialistUseCase createSpecialistUseCase,
            GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase,
            AddSpecialistToOrganizationUseCase addSpecialistToOrganizationUseCase
    ) {
        this.createSpecialistUseCase = createSpecialistUseCase;
        this.getSpecialistOrganizationsUseCase = getSpecialistOrganizationsUseCase;
        this.addSpecialistToOrganizationUseCase = addSpecialistToOrganizationUseCase;
    }

    @PostMapping("/specialist")
    public ResponseEntity<SpecialistDto> createSpecialistFromMe(
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

    @PostMapping("/organizations/{organizationId}")
    public ResponseEntity<OrganizationMemberDto> addMeToOrganization(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @PathVariable("organizationId") Long organizationId
    ) {
        OrganizationMember result = addSpecialistToOrganizationUseCase.execute(organizationId, currentUser.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
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
