package com.tzuchi.queueingsystem.controller;

import com.tzuchi.queueingsystem.entity.*;
import com.tzuchi.queueingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
        patient.setInQueue(true);
        Row2 savedPatient = row2Repository.save(patient);

        RegistrationStation registration = new RegistrationStation();
        registration.setPatientId(patient.getPatientId());
        registration.setSectionNumber(2);
        registration.setRegisteredTime(LocalDateTime.parse(date + "T00:00:00"));

        RegistrationStation savedRegistration = registrationStationRepository.save(registration);

        return ResponseEntity.ok(savedRegistration.getRegisteredSequence());
    }

    @GetMapping("/row2")
    public ResponseEntity<?> getRow2Queue() {
        return ResponseEntity.ok(row2Repository.findAll());
    }

    @PostMapping("/register/patientE")
    public ResponseEntity<?> registerPatientE(@RequestBody Row5 patient) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        patient.setInQueue(true);
        patient.setRegisteredTime(currentDateTime);
        Row5 savedPatient = row5Repository.save(patient);

        RegistrationStation registration = new RegistrationStation();
        registration.setPatientId(patient.getPatientId());
        registration.setSectionNumber(5);
        registration.setRegisteredTime(currentDateTime);

        RegistrationStation savedRegistration = registrationStationRepository.save(registration);

        return ResponseEntity.ok(savedRegistration.getRegisteredSequence());
    }

    @GetMapping("/row5")
    public ResponseEntity<?> getRow5Queue() {
        return ResponseEntity.ok(row5Repository.findAll());
    }

    @PostMapping("/register/patientD")
    public ResponseEntity<?> registerPatientD(@RequestBody Row8 patient, @RequestParam String date) {
        patient.setInQueue(true);
        Row8 savedPatient = row8Repository.save(patient);

        RegistrationStation registration = new RegistrationStation();
        registration.setPatientId(patient.getPatientId());
        registration.setSectionNumber(8);
        registration.setRegisteredTime(LocalDateTime.parse(date + "T00:00:00"));

        RegistrationStation savedRegistration = registrationStationRepository.save(registration);

        return ResponseEntity.ok(savedRegistration.getRegisteredSequence());
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

    @PostMapping("/call/nextB")
    public ResponseEntity<?> callNextB() {
        Row6 patient = row6Repository.findFirstByInQueueTrueOrderByPatientNumberAsc();
        if (patient != null) {
            return ResponseEntity.ok(patient.getPatientId());
        }
        return ResponseEntity.notFound().build();
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