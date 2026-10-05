package io.slotum.backend.api.organization;

import io.slotum.backend.api.organization.dto.CreateOrganizationRequest;
import io.slotum.backend.api.organization.dto.OrganizationDto;
import io.slotum.backend.api.organization.dto.OrganizationMemberDto;
import io.slotum.backend.api.organization.dto.SpecialistDto;
import io.slotum.backend.application.usecase.organization.*;
import io.slotum.backend.application.usecase.organizationMember.AddSpecialistToOrganizationUseCase;
import io.slotum.backend.application.usecase.organizationMember.GetOrganizationSpecialistsUseCase;
import io.slotum.backend.application.usecase.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class OrganizationController implements OrganizationApi {
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

    @Override
    public ResponseEntity<OrganizationDto> create(CreateOrganizationRequest request) {
        Organization result = createOrganizationUseCase.execute(
                new CreateOrganizationUseCase.Command(
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
    public ResponseEntity<OrganizationMemberDto> createOrganizationMember(
            Long organizationId,
            Long specialistUserId
    ) {
        OrganizationMember result = addSpecialistToOrganizationUseCase.execute(organizationId, specialistUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
                )
        );
    }

    @Override
    public ResponseEntity<OrganizationDto> getOrganization(Long id) {
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

    @Override
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

    @Override
    public ResponseEntity<List<SpecialistDto>> getOrganizationSpecialists(Long organizationId) {
        List<Specialist> result = getOrganizationSpecialistsUseCase.execute(organizationId);

        return ResponseEntity.status(HttpStatus.OK).body(
                result.stream().map(specialist -> new SpecialistDto(
                        specialist.getUserId(),
                        specialist.getDescription(),
                        specialist.getGrade()
                )).toList()
        );
    }

    @Override
    public ResponseEntity<OrganizationDto> deleteOrganization(Long id) {
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

    @Override
    public ResponseEntity<OrganizationMemberDto> deleteSpecialistFromOrganization(
            Long organizationId,
            Long specialistUserId
    ) {
        OrganizationMember result = removeSpecialistFromOrganizationUseCase.execute(organizationId, specialistUserId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new OrganizationMemberDto(
                        result.getOrganizationId(),
                        result.getSpecialistUserId()
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteAllOrganizations() {
        deleteAllOrganizationUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
