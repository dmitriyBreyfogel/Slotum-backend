package io.slotum.backend.application.usecase.resource;

import io.slotum.backend.domain.resource.ResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllResourcesUseCase {
    private final ResourceRepository resourceRepository;

    public DeleteAllResourcesUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public void execute() {
        resourceRepository.deleteAll();
    }
}
