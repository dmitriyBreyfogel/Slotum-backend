package io.slotum.backend.api.me;

import io.slotum.backend.api.me.dto.CreateOrganizationRequest;
import io.slotum.backend.api.me.dto.CreateSpecialistRequest;
import io.slotum.backend.api.me.dto.OrganizationDto;
import io.slotum.backend.api.me.dto.OrganizationMemberDto;
import io.slotum.backend.api.me.dto.SpecialistDto;
import io.slotum.backend.application.usecase.organization.CreateMyOrganizationUseCase;
import io.slotum.backend.application.usecase.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.usecase.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.application.usecase.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class MeController implements MeApi {
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

    @Override
    public ResponseEntity<SpecialistDto> createSpecialistFromMe(
            AuthenticatedUser currentUser,
            CreateSpecialistRequest request
    ) {
          Specialist result = createSpecialistUseCase.execute(
                  new CreateSpecialistUseCase.Command(
                          currentUser.userId(),
                          request.description()
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

    @Override
    public ResponseEntity<OrganizationDto> createMyOrganization(
            AuthenticatedUser currentUser,
            CreateOrganizationRequest request
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

    @Override
    public ResponseEntity<List<OrganizationDto>> getMyOrganizations(AuthenticatedUser currentUser) {
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

    @Override
    public ResponseEntity<OrganizationMemberDto> removeMeFromOrganization(
            AuthenticatedUser currentUser,
            Long organizationId
    ) {
        OrganizationMember result = removeSpecialistFromOrganizationUseCase.execute(organizationId, currentUser.userId());

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
                )
        );
    }

}
