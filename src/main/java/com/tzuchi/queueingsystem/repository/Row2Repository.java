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

    // Find all patients in queue, ordered by priority and registration time
    @Query("SELECT r FROM Row2 r WHERE r.inQueue = true ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.registeredTime ASC")
    List<Row2> findAllByInQueueTrueOrderByPriorityDescRegisteredTimeAsc();

    // Find the first patient in queue by priority and patient number
    Row2 findFirstByInQueueTrueOrderByPriorityAscPatientNumberAsc();

    // Find the maximum patient number for a given category
    @Query("SELECT MAX(r.patientNumber) FROM Row2 r WHERE r.patientCategory = :category")
    Integer findMaxPatientNumberByCategory(@Param("category") char category);

    // Find all patients in queue
    List<Row2> findAllByInQueueTrue();

    // Find all patients in queue, ordered by registration time
    List<Row2> findAllByInQueueTrueOrderByRegisteredTimeAsc();

    // Find patients by category
    List<Row2> findByPatientCategory(char category);

    // Find patients registered after a certain time
    List<Row2> findByRegisteredTimeAfter(LocalDateTime time);

    // Find patients by priority
    List<Row2> findByPriority(String priority);

    // Count patients in queue
    long countByInQueueTrue();

    // Custom query to find patients with a specific condition
    @Query("SELECT r FROM Row2 r WHERE r.inQueue = true AND r.priority = :priority AND r.patientCategory = :category")
    List<Row2> findPatientsInQueueByPriorityAndCategory(@Param("priority") String priority, @Param("category") char category);

    // Delete patients that are not in queue and registered before a certain time
    @Query("DELETE FROM Row2 r WHERE r.inQueue = false AND r.registeredTime < :time")
    void deleteOldPatients(@Param("time") LocalDateTime time);
}