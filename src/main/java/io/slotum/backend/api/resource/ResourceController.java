package io.slotum.backend.api.resource;

import io.slotum.backend.api.resource.dto.CreateResourceRequest;
import io.slotum.backend.api.resource.dto.ResourceDto;
import io.slotum.backend.application.resource.CreateResourceUseCase;
import io.slotum.backend.application.resource.DeleteAllResourcesUseCase;
import io.slotum.backend.application.resource.DeleteByIdResourceUseCase;
import io.slotum.backend.application.resource.GetAllResourcesUseCase;
import io.slotum.backend.application.resource.GetResourceUseCase;
import io.slotum.backend.domain.resource.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class ResourceController implements ResourceApi {
    private final CreateResourceUseCase createResourceUseCase;
    private final GetResourceUseCase getResourceUseCase;
    private final GetAllResourcesUseCase getAllResourcesUseCase;
    private final DeleteByIdResourceUseCase deleteByIdResourceUseCase;
    private final DeleteAllResourcesUseCase deleteAllResourcesUseCase;

    public ResourceController(
            CreateResourceUseCase createResourceUseCase,
            GetResourceUseCase getResourceUseCase,
            GetAllResourcesUseCase getAllResourcesUseCase,
            DeleteByIdResourceUseCase deleteByIdResourceUseCase,
            DeleteAllResourcesUseCase deleteAllResourcesUseCase
    ) {
        this.createResourceUseCase = createResourceUseCase;
        this.getResourceUseCase = getResourceUseCase;
        this.getAllResourcesUseCase = getAllResourcesUseCase;
        this.deleteByIdResourceUseCase = deleteByIdResourceUseCase;
        this.deleteAllResourcesUseCase = deleteAllResourcesUseCase;
    }

    @Override
    public ResponseEntity<ResourceDto> create(CreateResourceRequest request) {
        Resource result = createResourceUseCase.execute(
                new CreateResourceUseCase.Command(
                        request.httpMethod(),
                        request.urlPattern(),
                        request.description()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @Override
    public ResponseEntity<ResourceDto> getResource(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getResourceUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<List<ResourceDto>> getAllResources() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllResourcesUseCase.execute().stream()
                        .map(ResourceController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<ResourceDto> deleteResource(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(deleteByIdResourceUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAllResources() {
        deleteAllResourcesUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private static ResourceDto toDto(Resource source) {
        return new ResourceDto(
                source.getId(),
                source.getHttpMethod(),
                source.getUrlPattern(),
                source.getDescription()
        );
    }
}
