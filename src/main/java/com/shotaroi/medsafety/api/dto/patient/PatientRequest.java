package com.shotaroi.medsafety.api.dto.patient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank @Size(max = 20) @Pattern(regexp = "^\\d{6,8}-?\\d{0,4}$", message = "personalNumber: simple format YYYYMMDD or YYYYMMDD-XXXX") String personalNumber,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull LocalDate dateOfBirth
) {}
