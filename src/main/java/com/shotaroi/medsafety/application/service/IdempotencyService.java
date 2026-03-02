package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.domain.entity.IdempotencyKey;
import com.shotaroi.medsafety.domain.exception.IdempotencyConflictException;
import com.shotaroi.medsafety.infrastructure.persistence.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;

    public IdempotencyService(IdempotencyKeyRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<IdempotencyResult> findExisting(String key, String requestBody) {
        return repository.findByIdempotencyKey(key)
                .map(stored -> {
                    String requestHash = hashRequest(requestBody);
                    if (!stored.getRequestHash().equals(requestHash)) {
                        throw new IdempotencyConflictException(key);
                    }
                    return new IdempotencyResult(stored.getResponseStatus(), stored.getResponseBody());
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void store(String key, String requestBody, int responseStatus, String responseBody) {
        IdempotencyKey entity = new IdempotencyKey();
        entity.setIdempotencyKey(key);
        entity.setRequestHash(hashRequest(requestBody));
        entity.setResponseStatus(responseStatus);
        entity.setResponseBody(responseBody);
        repository.save(entity);
    }

    private String hashRequest(String body) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((body != null ? body : "").getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public record IdempotencyResult(int status, String body) {}
}
