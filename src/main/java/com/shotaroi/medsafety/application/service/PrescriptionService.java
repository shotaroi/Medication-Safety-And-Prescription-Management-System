package com.shotaroi.medsafety.application.service;

import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleRequest;
import com.shotaroi.medsafety.api.dto.prescription.DosageScheduleResponse;
import com.shotaroi.medsafety.api.dto.prescription.InteractionWarning;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionRequest;
import com.shotaroi.medsafety.api.dto.prescription.PrescriptionResponse;
import com.shotaroi.medsafety.api.mapper.PrescriptionMapper;
import com.shotaroi.medsafety.domain.entity.DosageSchedule;
import com.shotaroi.medsafety.domain.entity.Medication;
import com.shotaroi.medsafety.domain.entity.Prescription;
import com.shotaroi.medsafety.domain.enums.AggregateType;
import com.shotaroi.medsafety.domain.enums.AuditAction;
import com.shotaroi.medsafety.domain.enums.PrescriptionStatus;
import com.shotaroi.medsafety.domain.exception.MaxDailyDoseExceededException;
import com.shotaroi.medsafety.domain.exception.PrescriptionOverlapException;
import com.shotaroi.medsafety.domain.exception.ResourceNotFoundException;
import com.shotaroi.medsafety.infrastructure.persistence.DosageScheduleRepository;
import com.shotaroi.medsafety.infrastructure.persistence.MedicationRepository;
import com.shotaroi.medsafety.infrastructure.persistence.PatientRepository;
import com.shotaroi.medsafety.infrastructure.persistence.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DosageScheduleRepository scheduleRepository;
    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionMapper mapper;
    private final AuditService auditService;
    private final InteractionService interactionService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               DosageScheduleRepository scheduleRepository,
                               MedicationRepository medicationRepository,
                               PatientRepository patientRepository,
                               PrescriptionMapper mapper,
                               AuditService auditService,
                               InteractionService interactionService) {
        this.prescriptionRepository = prescriptionRepository;
        this.scheduleRepository = scheduleRepository;
        this.medicationRepository = medicationRepository;
        this.patientRepository = patientRepository;
        this.mapper = mapper;
        this.auditService = auditService;
        this.interactionService = interactionService;
    }

    @Transactional
    public PrescriptionResponse create(PrescriptionRequest request, String performedBy) {
        // Validate patient and medication exist
        patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.patientId()));
        Medication medication = medicationRepository.findById(request.medicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Medication", request.medicationId()));

        // Check overlap
        LocalDate start = request.startDate() != null ? request.startDate() : LocalDate.now();
        LocalDate end = request.endDate() != null ? request.endDate() : LocalDate.MAX;
        List<Prescription> overlapping = prescriptionRepository.findOverlappingActive(
                request.patientId(), request.medicationId(), PrescriptionStatus.ACTIVE, start, end);
        if (!overlapping.isEmpty()) {
            throw new PrescriptionOverlapException(request.patientId(), request.medicationId());
        }

        // Check interactions (block on HIGH)
        List<InteractionWarning> warnings = interactionService.checkForPatient(
                request.patientId(), request.medicationId(), true);

        // Create prescription
        Prescription prescription = new Prescription();
        prescription.setPatientId(request.patientId());
        prescription.setMedicationId(request.medicationId());
        prescription.setPrescribedBy(request.prescribedBy());
        prescription.setInstructions(request.instructions());
        prescription.setStatus(PrescriptionStatus.ACTIVE);
        prescription.setStartDate(request.startDate());
        prescription.setEndDate(request.endDate());
        prescription = prescriptionRepository.save(prescription);

        // Validate and add schedules
        List<DosageScheduleResponse> scheduleResponses = new ArrayList<>();
        if (request.schedules() != null && !request.schedules().isEmpty()) {
            for (DosageScheduleRequest s : request.schedules()) {
                validateMaxDailyDose(s, medication.getMaxDailyDoseMg());
                DosageSchedule schedule = mapper.toScheduleEntity(s);
                schedule.setPrescriptionId(prescription.getId());
                schedule = scheduleRepository.save(schedule);
                scheduleResponses.add(mapper.toScheduleResponse(schedule));
            }
        }

        auditService.audit(AggregateType.PRESCRIPTION, prescription.getId(), AuditAction.CREATED, performedBy,
                Map.of("patientId", prescription.getPatientId(), "medicationId", prescription.getMedicationId()));

        return mapper.toResponse(prescription, scheduleResponses, warnings);
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse getById(UUID id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));
        List<DosageScheduleResponse> schedules = scheduleRepository.findByPrescriptionId(id)
                .stream()
                .map(mapper::toScheduleResponse)
                .collect(Collectors.toList());
        return mapper.toResponse(prescription, schedules);
    }

    @Transactional
    public PrescriptionResponse cancel(UUID id, String performedBy) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", id));
        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescription = prescriptionRepository.save(prescription);
        auditService.audit(AggregateType.PRESCRIPTION, id, AuditAction.CANCELLED, performedBy,
                Map.of("prescriptionId", id));
        List<DosageScheduleResponse> schedules = scheduleRepository.findByPrescriptionId(id)
                .stream()
                .map(mapper::toScheduleResponse)
                .collect(Collectors.toList());
        return mapper.toResponse(prescription, schedules);
    }

    @Transactional
    public DosageScheduleResponse addSchedule(UUID prescriptionId, DosageScheduleRequest request, String performedBy) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", prescriptionId));
        Medication medication = medicationRepository.findById(prescription.getMedicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Medication", prescription.getMedicationId()));

        validateMaxDailyDose(request, medication.getMaxDailyDoseMg());

        DosageSchedule schedule = mapper.toScheduleEntity(request);
        schedule.setPrescriptionId(prescriptionId);
        schedule = scheduleRepository.save(schedule);

        auditService.audit(AggregateType.SCHEDULE, schedule.getId(), AuditAction.CREATED, performedBy,
                Map.of("prescriptionId", prescriptionId, "doseMg", request.doseMg(), "timesPerDay", request.timesPerDay()));

        return mapper.toScheduleResponse(schedule);
    }

    public List<PrescriptionResponse> findByPatientId(UUID patientId) {
        return prescriptionRepository.findByPatientId(patientId)
                .stream()
                .map(p -> {
                    List<DosageScheduleResponse> schedules = scheduleRepository.findByPrescriptionId(p.getId())
                            .stream()
                            .map(mapper::toScheduleResponse)
                            .collect(Collectors.toList());
                    return mapper.toResponse(p, schedules);
                })
                .collect(Collectors.toList());
    }

    private void validateMaxDailyDose(DosageScheduleRequest request, int maxDailyDoseMg) {
        int dailyDose = request.doseMg() * request.timesPerDay();
        if (dailyDose > maxDailyDoseMg) {
            throw new MaxDailyDoseExceededException(dailyDose, maxDailyDoseMg);
        }
    }
}
