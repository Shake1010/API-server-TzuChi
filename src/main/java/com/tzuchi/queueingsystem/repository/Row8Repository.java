package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row6;
import com.tzuchi.queueingsystem.entity.Row8;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Row8Repository extends JpaRepository<Row8, String> {
    Row8 findFirstByInQueueTrueOrderByPatientNumberAsc();

    @Query("SELECT MAX(r.patientNumber) FROM Row8 r")
    Integer findMaxPatientNumber();



    @Query("SELECT r FROM Row8 r ORDER BY r.patientNumber ASC")
    List<Row8> findAllSortedByPatientNumber();

    @Query("SELECT r FROM Row8 r WHERE r.inQueue = true ORDER BY r.patientNumber ASC")
    List<Row8> findAllByInQueueTrueOrderByPatientNumberAsc();

}