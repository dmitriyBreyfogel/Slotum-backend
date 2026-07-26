package io.slotum.backend.application.resource;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.domain.resource.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllResourcesUseCase {
    private final ResourceRepository resourceRepository;

    public GetAllResourcesUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> execute() {
        return resourceRepository.findAll();
    }
}
