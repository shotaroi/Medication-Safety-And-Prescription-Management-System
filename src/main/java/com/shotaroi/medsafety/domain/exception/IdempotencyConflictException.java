package com.shotaroi.medsafety.domain.exception;

public class IdempotencyConflictException extends DomainException {

    private final String idempotencyKey;

    public IdempotencyConflictException(String idempotencyKey) {
        super("Idempotency key already used with different request: " + idempotencyKey);
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
