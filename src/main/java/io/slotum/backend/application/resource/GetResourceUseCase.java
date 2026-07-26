package io.slotum.backend.application.resource;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.domain.resource.ResourceRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetResourceUseCase {
    private final ResourceRepository resourceRepository;

    public GetResourceUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource execute(Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);

        if (resource.isEmpty()) {
            throw AppException.build(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Resource not found",
                    Map.of("id", id)
            );
        }

        return resource.get();
    }
}
