package com.shotaroi.medsafety.domain.exception;

import java.util.UUID;

public class ResourceNotFoundException extends DomainException {

    private final String resourceType;
    private final UUID id;

    public ResourceNotFoundException(String resourceType, UUID id) {
        super(resourceType + " not found: " + id);
        this.resourceType = resourceType;
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public UUID getId() {
        return id;
    }
}
