package com.tzuchi.queueingsystem.controller;

import com.tzuchi.queueingsystem.entity.*;
import com.tzuchi.queueingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QueueController {
    private final RegistrationStationRepository registrationStationRepository;
    private final Row2Repository row2Repository;
    private final Row5Repository row5Repository;
    private final Row6Repository row6Repository;
    private final Row8Repository row8Repository;

    @PostMapping("/register/row2-patient")
    public ResponseEntity<?> registerRow2Patient(@RequestBody Row2 patient, @RequestParam String date) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.parse(date + "T00:00:00");

            // Validate and set patient category and priority
            if (patient.getPatientCategory() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Patient category must be provided"));
            }

            switch (patient.getPatientCategory()) {
                case 'P':
                    patient.setPriority(Row2.Priority.HIGH);
                    break;
                case 'A':
                    patient.setPriority(Row2.Priority.MID);
                    break;
                case 'W':
                    patient.setPriority(Row2.Priority.LOW);
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid patient category"));
            }

            // Generate and set the patient number
            Integer maxPatientNumber = row2Repository.findMaxPatientNumber();
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;
            patient.setPatientNumber(nextNumber);

            // Generate patient ID
            String patientId = String.valueOf(patient.getPatientCategory()) + nextNumber;
            patient.setPatientId(patientId);

            // Set other required fields
            patient.setInQueue(true);
            patient.setSectionNumber(2);

            Row2 savedPatient = row2Repository.save(patient);

            RegistrationStation registration = new RegistrationStation();
            registration.setPatientId(savedPatient.getPatientId());
            registration.setSectionNumber(savedPatient.getSectionNumber());
            registration.setRegisteredTime(currentDateTime);

            RegistrationStation savedRegistration = registrationStationRepository.save(registration);

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("patientId", patientId);
            response.put("registeredSequence", savedRegistration.getRegisteredSequence());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred while registering the patient: " + e.getMessage()));
        }
    }


    @GetMapping("/row2")
    public ResponseEntity<?> getRow2Queue() {
        return ResponseEntity.ok(row2Repository.findAll());
    }

    @PostMapping("/register/patientE")
    public ResponseEntity<?> registerPatientE(@RequestParam(required = false) String date) {
        try {
            LocalDateTime currentDateTime = (date != null) ?
                    LocalDateTime.parse(date + "T00:00:00") : LocalDateTime.now();

            // Get the next patient number
            Integer maxPatientNumber = row5Repository.findMaxPatientNumber();
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;

            // Create patient ID
            String patientId = "E" + nextNumber;

            // Create and save Row5 patient
            Row5 patient = new Row5();
            patient.setPatientId(patientId);
            patient.setPatientNumber(nextNumber);
            patient.setInQueue(true);
            patient.setRegisteredTime(currentDateTime);
            Row5 savedPatient = row5Repository.save(patient);

            // Create and save RegistrationStation
            RegistrationStation registration = new RegistrationStation();
            registration.setPatientId(patientId);
            registration.setSectionNumber(5);
            registration.setRegisteredTime(currentDateTime);
            RegistrationStation savedRegistration = registrationStationRepository.save(registration);

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("patientId", patientId);
            response.put("registeredSequence", savedRegistration.getRegisteredSequence());
            response.put("patientNumber", nextNumber);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred while registering the patient: " + e.getMessage()));
        }
    }


    @GetMapping("/row5")
    public ResponseEntity<?> getRow5Queue() {
        return ResponseEntity.ok(row5Repository.findAll());
    }

    @PostMapping("/register/patientD")
    public ResponseEntity<?> registerPatientD(@RequestParam String date) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.parse(date + "T00:00:00");

            // Get the next patient number
            Integer maxPatientNumber = row8Repository.findMaxPatientNumber();
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;

            // Create patient ID
            String patientId = "D" + nextNumber;

            // Create and save Row8 patient
            Row8 patient = new Row8();
            patient.setPatientId(patientId);
            patient.setPatientNumber(nextNumber);
            patient.setInQueue(true);
            patient.setRegisteredTime(currentDateTime);
            Row8 savedPatient = row8Repository.save(patient);

            // Create and save RegistrationStation
            RegistrationStation registration = new RegistrationStation();
            registration.setPatientId(patientId);
            registration.setSectionNumber(8);
            registration.setRegisteredTime(currentDateTime);
            RegistrationStation savedRegistration = registrationStationRepository.save(registration);

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("patientId", patientId);
            response.put("registeredSequence", savedRegistration.getRegisteredSequence());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("An error occurred while registering the patient: " + e.getMessage());
        }
    }

    @GetMapping("/row8")
    public ResponseEntity<?> getRow8Queue() {
        return ResponseEntity.ok(row8Repository.findAll());
    }

    @PostMapping("/call/highest")
    public ResponseEntity<?> callHighestPriority() {
        Row2 patient = row2Repository.findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();
        if (patient != null) {
            return ResponseEntity.ok(patient.getPatientId());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/call/nextE")
    public ResponseEntity<?> callNextE() {
        Row5 patient = row5Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (patient != null) {
            return ResponseEntity.ok(patient.getPatientId());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/call/nextD")
    public ResponseEntity<?> callNextD() {
        Row8 patient = row8Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (patient != null) {
            return ResponseEntity.ok(patient.getPatientId());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/register/patientB")
    public ResponseEntity<?> registerPatientB(@RequestParam String date) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.parse(date + "T00:00:00");

            // Get the next patient number
            Integer maxPatientNumber = row6Repository.findMaxPatientNumber();
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;

            // Create patient ID
            String patientId = "B" + nextNumber;

            // Create and save Row6 patient
            Row6 patient = new Row6();
            patient.setPatientId(patientId);
            patient.setPatientNumber(nextNumber);
            patient.setInQueue(true);
            patient.setRegisteredTime(currentDateTime);
            Row6 savedPatient = row6Repository.save(patient);

            // Create and save RegistrationStation
            RegistrationStation registration = new RegistrationStation();
            registration.setPatientId(patientId);
            registration.setSectionNumber(6);
            registration.setRegisteredTime(currentDateTime);
            RegistrationStation savedRegistration = registrationStationRepository.save(registration);

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("patientId", patientId);
            response.put("registeredSequence", savedRegistration.getRegisteredSequence());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("An error occurred while registering the patient: " + e.getMessage());
        }
    }
    @GetMapping("/get/allRegister")
    public ResponseEntity<?> getAllRegistrations() {
        return ResponseEntity.ok(registrationStationRepository.findAll());
    }

    @PutMapping("/withdraw-row2")
    public ResponseEntity<?> withdrawRow2(@RequestParam String patientId) {
        Row2 patient = row2Repository.findById(patientId).orElse(null);
        if (patient != null) {
            patient.setInQueue(false);
            row2Repository.save(patient);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row5")
    public ResponseEntity<?> withdrawRow5(@RequestParam String patientId) {
        Row5 patient = row5Repository.findById(patientId).orElse(null);
        if (patient != null) {
            patient.setInQueue(false);
            row5Repository.save(patient);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row8")
    public ResponseEntity<?> withdrawRow8(@RequestParam String patientId) {
        Row8 patient = row8Repository.findById(patientId).orElse(null);
        if (patient != null) {
            patient.setInQueue(false);
            row8Repository.save(patient);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row6")
    public ResponseEntity<?> withdrawRow6(@RequestParam String patientId) {
        Row6 patient = row6Repository.findById(patientId).orElse(null);
        if (patient != null) {
            patient.setInQueue(false);
            row6Repository.save(patient);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

