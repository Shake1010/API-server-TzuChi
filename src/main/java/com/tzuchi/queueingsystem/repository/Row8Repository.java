package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row8;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Row8Repository extends JpaRepository<Row8, String> {
    Row8 findFirstByInQueueTrueOrderByPatientNumberAsc();

    @Query("SELECT MAX(r.patientNumber) FROM Row8 r")
    Integer findMaxPatientNumber();
}