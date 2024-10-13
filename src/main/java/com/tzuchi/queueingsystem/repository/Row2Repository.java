package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Row2Repository extends JpaRepository<Row2, String> {
    Row2 findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();

    @Query("SELECT MAX(r.patientNumber) FROM Row2 r WHERE r.patientCategory = :category")
    Integer findMaxPatientNumberByCategory(@Param("category") char category);
}