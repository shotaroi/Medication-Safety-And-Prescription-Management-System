package com.shotaroi.medsafety.domain.exception;

public class DuplicateResourceException extends DomainException {

    private final String field;
    private final String value;

    public DuplicateResourceException(String field, String value) {
        super("Resource already exists with " + field + ": " + value);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
