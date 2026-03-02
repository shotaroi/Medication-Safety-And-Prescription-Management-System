package com.shotaroi.medsafety.common;

import com.shotaroi.medsafety.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.builder()
                        .title("Resource Not Found")
                        .status(404)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of("resourceType", ex.getResourceType(), "id", ex.getId().toString()))
                        .build());
    }

    @ExceptionHandler(MaxDailyDoseExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxDailyDose(MaxDailyDoseExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ProblemDetail.builder()
                        .title("Max Daily Dose Exceeded")
                        .status(422)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of(
                                "dailyDoseMg", ex.getDailyDoseMg(),
                                "maxDailyDoseMg", ex.getMaxDailyDoseMg()))
                        .build());
    }

    @ExceptionHandler(PrescriptionOverlapException.class)
    public ResponseEntity<ProblemDetail> handleOverlap(PrescriptionOverlapException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ProblemDetail.builder()
                        .title("Prescription Overlap")
                        .status(409)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of(
                                "patientId", ex.getPatientId().toString(),
                                "medicationId", ex.getMedicationId().toString()))
                        .build());
    }

    @ExceptionHandler(HighSeverityInteractionException.class)
    public ResponseEntity<ProblemDetail> handleHighInteraction(HighSeverityInteractionException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ProblemDetail.builder()
                        .title("High Severity Drug Interaction")
                        .status(422)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of("messages", ex.getMessages()))
                        .build());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ProblemDetail.builder()
                        .title("Duplicate Resource")
                        .status(409)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of("field", ex.getField(), "value", ex.getValue()))
                        .build());
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ProblemDetail> handleIdempotencyConflict(IdempotencyConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ProblemDetail.builder()
                        .title("Idempotency Key Conflict")
                        .status(409)
                        .detail(ex.getMessage())
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of("idempotencyKey", ex.getIdempotencyKey()))
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(e -> e.getField(), e -> e.getDefaultMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.builder()
                        .title("Validation Failed")
                        .status(400)
                        .detail("Request validation failed")
                        .correlationId(CorrelationIdHolder.get())
                        .extensions(Map.of("errors", errors))
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.builder()
                        .title("Internal Server Error")
                        .status(500)
                        .detail("An unexpected error occurred")
                        .correlationId(CorrelationIdHolder.get())
                        .build());
    }
}
