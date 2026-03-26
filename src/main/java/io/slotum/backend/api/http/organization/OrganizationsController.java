package io.slotum.backend.api.http.organization;

import io.slotum.backend.application.organization.CreateOrganizationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationsController {
    private final CreateOrganizationUseCase createOrganizationUseCase;

    public OrganizationsController(CreateOrganizationUseCase createOrganizationUseCase) {
        this.createOrganizationUseCase = createOrganizationUseCase;
    }

    @PostMapping
    public ResponseEntity<RegisterOrganizationResponse> create(@RequestBody RegisterOrganizationRequest request) {
        CreateOrganizationUseCase.Result result = createOrganizationUseCase.execute(
                new CreateOrganizationUseCase.Command(
                        request.name,
                        request.description
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterOrganizationResponse(
                        result.id(),
                        result.name()
                )
        );
    }

    public record RegisterOrganizationRequest(String name, String description) {}

    public record RegisterOrganizationResponse(Long id, String name) {}
}
