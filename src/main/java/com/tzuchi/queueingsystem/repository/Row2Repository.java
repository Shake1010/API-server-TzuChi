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
    @Query("SELECT r FROM Row2 r ORDER BY " +
            "CASE r.priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MID' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "ELSE 4 END, r.patientNumber ASC")
    List<Row2> findAllOrderByPriorityDescPatientNumberAsc();


    // Find the first patient in queue by priority and patient number
    Row2 findFirstByInQueueTrueOrderByPriorityAscPatientNumberAsc();

    // Find the maximum patient number for a given category
    @Query("SELECT MAX(r.patientNumber) FROM Row2 r WHERE r.patientCategory = :category")
    Integer findMaxPatientNumberByCategory(@Param("category") char category);


}