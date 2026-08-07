package io.slotum.backend.api.specialist;

import io.slotum.backend.api.specialist.dto.CreateSpecialistRequest;
import io.slotum.backend.api.specialist.dto.OrganizationDto;
import io.slotum.backend.api.specialist.dto.SpecialistDto;
import io.slotum.backend.application.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.specialist.*;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.specialist.Specialist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class SpecialistController implements SpecialistApi {
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

    @Override
    public ResponseEntity<SpecialistDto> createSpecialist(CreateSpecialistRequest request) {
        Specialist result = createSpecialistUseCase.execute(
                new CreateSpecialistUseCase.Command(
                    request.userId(),
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
    public ResponseEntity<SpecialistDto> getSpecialist(long id) {
        Specialist result = getSpecialistUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SpecialistDto(
                        result.getUserId(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @Override
    public ResponseEntity<List<OrganizationDto>> getSpecialistOrganizations(Long specialistUserId) {
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

    @Override
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

    @Override
    public ResponseEntity<SpecialistDto> deleteSpecialist(Long id) {
        Specialist result = deleteByIdSpecialistUseCase.execute(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new SpecialistDto(
                        result.getUserId(),
                        result.getDescription(),
                        result.getGrade()
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteAllSpecialists() {
        deleteAllSpecialistUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
