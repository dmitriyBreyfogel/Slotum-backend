package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.infrastructure.jpa.entities.ResourceJpa;

public final class ResourceJpaMapper {
    private ResourceJpaMapper() {}

    public static Resource toDomain(ResourceJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("ResourceJpa source is null");
        }

        return Resource.restore(
                source.getId(),
                source.getHttpMethod(),
                source.getUrlPattern(),
                source.getDescription()
        );
    }

    public static ResourceJpa toJpa(Resource source) {
        if (source == null) {
            throw new IllegalArgumentException("Resource source is null");
        }

        return new ResourceJpa(
                source.getId(),
                source.getHttpMethod(),
                source.getUrlPattern(),
                source.getDescription()
        );
    }
}
