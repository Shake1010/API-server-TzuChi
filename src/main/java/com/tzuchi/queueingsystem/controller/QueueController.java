package com.tzuchi.queueingsystem.controller;

import com.tzuchi.queueingsystem.entity.*;
import com.tzuchi.queueingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QueueController {
    private final Row2Repository row2Repository;
    private final ObjectMapper objectMapper;
    private final RegistrationStationRepository registrationStationRepository;

    private final Row5Repository row5Repository;
    private final Row6Repository row6Repository;
    private final Row8Repository row8Repository;

    @PostMapping("/register/row2-patient")
    public ResponseEntity<?> registerRow2PatientScanning(@RequestBody Row2 patient,
                                                         @RequestParam String date) {
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

            Integer maxPatientNumber = row2Repository.findMaxPatientNumberByCategory(patient.getPatientCategory());
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;
            patient.setPatientNumber(nextNumber);

            String patientId = String.valueOf(patient.getPatientCategory()) + nextNumber;
            patient.setPatientId(patientId);

            // Set for Scanning Room
            patient.setInQueue(true);        // Patient is in scanning queue
            patient.setInQueueClinic(true); // is in clinic queue
            patient.setSectionNumber(2);
            patient.setRegisteredTime(currentDateTime);

            Row2 savedPatient = row2Repository.save(patient);

            RegistrationStation registration = new RegistrationStation();
            registration.setPatientId(patientId);
            registration.setSectionNumber(2);
            registration.setRegisteredTime(currentDateTime);
            RegistrationStation savedRegistration = registrationStationRepository.save(registration);

            Map<String, Object> response = new HashMap<>();
            response.put("patientId", savedPatient.getPatientId());
            response.put("registeredSequence", savedRegistration.getRegisteredSequence());
            response.put("patientNumber", savedPatient.getPatientNumber());
            response.put("queueType", "scanning");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred while registering the patient: " + e.getMessage()));
        }
    }



    // ... other methods

    @GetMapping("/row2")
    public ResponseEntity<?> getRow2Queue() {
        try {
            List<Row2> queue = row2Repository.findAllOrderByPriorityDescPatientNumberAsc();

            List<Map<String, Object>> patientList = new ArrayList<>();
            for (Row2 patient : queue) {
                Map<String, Object> patientMap = new HashMap<>();
                patientMap.put("patientId", patient.getPatientId());
                patientMap.put("inQueue", patient.isInQueue());
                patientList.add(patientMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sectionNumber", 2);
            response.put("patients", patientList);

            System.out.println("API Response for /row2: " + objectMapper.writeValueAsString(response));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while fetching Row 2 queue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/row5")
    public ResponseEntity<?> getRow5Queue() {
        List<Row5> queue = row5Repository.findAllSortedByPatientNumber();
        Map<String, Object> response = new HashMap<>();
        response.put("sectionNumber", 5);
        response.put("patients", queue);

        System.out.println("API Response for /row5: " + response);

        return ResponseEntity.ok(response);
    }




    @GetMapping("/row6")
    public ResponseEntity<?> getRow6Queue() {
        List<Row6> queue = row6Repository.findAllSortedByPatientNumber();
        Map<String, Object> response = new HashMap<>();
        response.put("sectionNumber", 6);
        response.put("patients", queue);

        System.out.println("API Response for /row6: " + response);

        return ResponseEntity.ok(response);
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
        List<Row8> queue = row8Repository.findAllSortedByPatientNumber();
        Map<String, Object> response = new HashMap<>();
        response.put("sectionNumber", 8);
        response.put("patients", queue);

        // Log the response
        System.out.println("API Response for /row8: " + response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/call/highest")
    public ResponseEntity<?> callHighestPriority() {
        try {
            Row2 currentPatient = row2Repository.findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();

            if (currentPatient == null) {
                return ResponseEntity.ok(Map.of("message", "No patients in queue"));
            }

            // Set current patient's inQueue to false
            currentPatient.setInQueue(false);
            row2Repository.save(currentPatient);

            // Find next patient after updating current patient's status
            Row2 nextPatient = row2Repository.findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();

            Map<String, Object> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error calling next patient: " + e.getMessage()));
        }
    }

    @PostMapping("/call/nextE")
    public ResponseEntity<?> callNextE() {
        Row5 currentPatient = row5Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (currentPatient != null) {
            // Set current patient's inQueue to false
            currentPatient.setInQueue(false);
            row5Repository.save(currentPatient);

            // Find next patient
            Row5 nextPatient = row5Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/call/nextB")
    public ResponseEntity<?> callNextB() {
        Row6 currentPatient = row6Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (currentPatient != null) {
            // Set current patient's inQueue to false
            currentPatient.setInQueue(false);
            row6Repository.save(currentPatient);

            // Find next patient
            Row6 nextPatient = row6Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/call/nextD")
    public ResponseEntity<?> callNextD() {
        Row8 currentPatient = row8Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (currentPatient != null) {
            // Set current patient's inQueue to false
            currentPatient.setInQueue(false);
            row8Repository.save(currentPatient);

            // Find next patient
            Row8 nextPatient = row8Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
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
            // Mark the current patient as not in queue
            patient.setInQueue(false);
            row2Repository.save(patient);

            // Get next patient in queue
            Row2 nextPatient = row2Repository.findFirstByInQueueTrueOrderByPriorityAscPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("withdrawnPatient", patientId);
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row5")
    public ResponseEntity<?> withdrawRow5(@RequestParam String patientId) {
        Row5 patient = row5Repository.findById(patientId).orElse(null);
        if (patient != null) {
            // Mark the current patient as not in queue
            patient.setInQueue(false);
            row5Repository.save(patient);

            // Get next patient in queue
            List<Row5> queuedPatients = row5Repository.findAllByInQueueTrueOrderByPatientNumberAsc();
            String nextPatientId = !queuedPatients.isEmpty() ? queuedPatients.get(0).getPatientId() : "";

            Map<String, String> response = new HashMap<>();
            response.put("withdrawnPatient", patientId);
            response.put("nextPatient", nextPatientId);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row8")
    public ResponseEntity<?> withdrawRow8(@RequestParam String patientId) {
        Row8 patient = row8Repository.findById(patientId).orElse(null);
        if (patient != null) {
            // Mark the current patient as not in queue
            patient.setInQueue(false);
            row8Repository.save(patient);

            // Get next patient in queue
            List<Row8> queuedPatients = row8Repository.findAllByInQueueTrueOrderByPatientNumberAsc();
            String nextPatientId = !queuedPatients.isEmpty() ? queuedPatients.get(0).getPatientId() : "";

            Map<String, String> response = new HashMap<>();
            response.put("withdrawnPatient", patientId);
            response.put("nextPatient", nextPatientId);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/withdraw-row6")
    public ResponseEntity<?> withdrawRow6(@RequestParam String patientId) {
        Row6 patient = row6Repository.findById(patientId).orElse(null);
        if (patient != null) {
            // Mark the current patient as not in queue
            patient.setInQueue(false);
            row6Repository.save(patient);

            // Get next patient in queue
            List<Row6> queuedPatients = row6Repository.findAllByInQueueTrueOrderByPatientNumberAsc();
            String nextPatientId = !queuedPatients.isEmpty() ? queuedPatients.get(0).getPatientId() : "";

            Map<String, String> response = new HashMap<>();
            response.put("withdrawnPatient", patientId);
            response.put("nextPatient", nextPatientId);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/row2/clinic")
    public ResponseEntity<?> getRow2ClinicQueue() {
        try {
            List<Row2> queue = row2Repository.findAllClinicOrderByPriorityDescPatientNumberAsc();

            List<Map<String, Object>> patientList = new ArrayList<>();
            for (Row2 patient : queue) {
                Map<String, Object> patientMap = new HashMap<>();
                patientMap.put("patientId", patient.getPatientId());
                patientMap.put("inQueueClinic", patient.getInQueueClinic());
                patientList.add(patientMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sectionNumber", 2);
            response.put("patients", patientList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching clinic queue: " + e.getMessage()));
        }
    }

    @PostMapping("/call/clinic/highest")
    public ResponseEntity<?> callClinicHighestPriority() {
        try {
            Row2 currentPatient = row2Repository.findFirstByInQueueClinicTrueOrderByPriorityDescPatientNumberAsc();

            if (currentPatient == null) {
                return ResponseEntity.ok(Map.of("message", "No patients in clinic queue"));
            }

            currentPatient.setInQueueClinic(false);
            row2Repository.save(currentPatient);

            Row2 nextPatient = row2Repository.findFirstByInQueueClinicTrueOrderByPriorityDescPatientNumberAsc();

            Map<String, Object> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error calling next clinic patient: " + e.getMessage()));
        }
    }

    // Modified registration methods for other rows to include clinic queue
    @PostMapping("/register/patientE")
    public ResponseEntity<?> registerPatientE(@RequestParam(required = false) String date,
                                              @RequestParam(defaultValue = "false") boolean isClinic) {
        try {
            LocalDateTime currentDateTime = (date != null) ?
                    LocalDateTime.parse(date + "T00:00:00") : LocalDateTime.now();

            Integer maxPatientNumber = row5Repository.findMaxPatientNumber();
            int nextNumber = (maxPatientNumber != null) ? maxPatientNumber + 1 : 1;
            String patientId = "E" + nextNumber;

            Row5 patient = new Row5();
            patient.setPatientId(patientId);
            patient.setPatientNumber(nextNumber);
            patient.setInQueue(true);
            patient.setInQueueClinic(isClinic);
            patient.setRegisteredTime(LocalDateTime.now());
            Row5 savedPatient = row5Repository.save(patient);

            // ... existing registration station logic ...

            Map<String, Object> response = new HashMap<>();
            response.put("patientId", patientId);
            response.put("registeredSequence", nextNumber);
            response.put("isClinic", isClinic);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "An error occurred while registering the patient: " + e.getMessage()));
        }
    }

    // Add new clinic queue endpoints for other rows
    @GetMapping("/row5/clinic")
    public ResponseEntity<?> getRow5ClinicQueue() {
        List<Row5> queue = row5Repository.findAllByInQueueClinicTrueOrderByPatientNumberAsc();
        Map<String, Object> response = new HashMap<>();
        response.put("sectionNumber", 5);
        response.put("patients", queue);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/call/clinic/nextE")
    public ResponseEntity<?> callClinicNextE() {
        Row5 currentPatient = row5Repository.findFirstByInQueueClinicTrueOrderByPatientNumberAsc();
        if (currentPatient != null) {
            currentPatient.setInQueueClinic(false);
            row5Repository.save(currentPatient);

            Row5 nextPatient = row5Repository.findFirstByInQueueClinicTrueOrderByPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("currentPatient", currentPatient.getPatientId());
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    // Similar modifications for Row6 and Row8...
    // Add withdraw endpoints for clinic queue
    @PutMapping("/withdraw-row2-clinic")
    public ResponseEntity<?> withdrawRow2Clinic(@RequestParam String patientId) {
        Row2 patient = row2Repository.findById(patientId).orElse(null);
        if (patient != null) {
            patient.setInQueueClinic(false);
            row2Repository.save(patient);

            Row2 nextPatient = row2Repository.findFirstByInQueueClinicTrueOrderByPriorityAscPatientNumberAsc();

            Map<String, String> response = new HashMap<>();
            response.put("withdrawnPatient", patientId);
            response.put("nextPatient", nextPatient != null ? nextPatient.getPatientId() : "");

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
