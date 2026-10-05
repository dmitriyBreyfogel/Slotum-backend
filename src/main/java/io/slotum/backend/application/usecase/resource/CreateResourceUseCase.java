package io.slotum.backend.application.usecase.resource;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.domain.resource.ResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateResourceUseCase {
    private final ResourceRepository resourceRepository;

    public CreateResourceUseCase(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Resource execute(Command command) {
        Resource resource = Resource.create(command.httpMethod(), command.urlPattern(), command.description());
        return resourceRepository.save(resource);
    }

    public record Command(
            String httpMethod,
            String urlPattern,
            String description
    ) {}
}
