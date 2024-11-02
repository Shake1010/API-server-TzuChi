package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface Row2Repository extends JpaRepository<Row2, String> {
    // Get all patients ordered by priority and patient number
    @Query("SELECT r FROM Row2 r WHERE r.inQueue = true ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.patientNumber ASC")
    List<Row2> findAllOrderByPriorityDescPatientNumberAsc();

    // Find the first patient in queue by priority and patient number (with LIMIT 1)
    @Query(value = "SELECT * FROM row2 r WHERE r.in_queue = true ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.patient_number ASC LIMIT 1", nativeQuery = true)
    Row2 findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();

    // Find the maximum patient number for a given category
    @Query("SELECT MAX(r.patientNumber) FROM Row2 r WHERE r.patientCategory = :category")
    Integer findMaxPatientNumberByCategory(@Param("category") char category);
    @Query("SELECT r FROM Row2 r WHERE r.inQueueClinic = true ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.patientNumber ASC")
    List<Row2> findAllClinicOrderByPriorityDescPatientNumberAsc();

    @Query(value = "SELECT * FROM row2 r WHERE r.in_queue_clinic = true ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.patient_number ASC LIMIT 1", nativeQuery = true)
    Row2 findFirstByInQueueClinicTrueOrderByPriorityDescPatientNumberAsc();

    Row2 findFirstByInQueueClinicTrueOrderByPriorityAscPatientNumberAsc();

    Row2 findFirstByInQueueTrueOrderByPriorityAscPatientNumberAsc();
    @Query(value = "SELECT * FROM row2 r WHERE r.in_queue = false " +
            "AND r.called_clinic_time IS NOT NULL " +
            "ORDER BY r.called_clinic_time DESC LIMIT 1", nativeQuery = true)
    Row2 findLatestCalledPatient();




}