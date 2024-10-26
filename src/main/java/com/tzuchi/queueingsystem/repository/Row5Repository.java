package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row5;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Row5Repository extends JpaRepository<Row5, String> {
    Row5 findFirstByInQueueTrueOrderByPatientNumberAsc();

    @Query("SELECT COALESCE(MAX(r.patientNumber), 0) FROM Row5 r")
    Integer findMaxPatientNumber();



    @Query("SELECT r FROM Row5 r ORDER BY r.patientNumber ASC")
    List<Row5> findAllSortedByPatientNumber();

    @Query("SELECT r FROM Row5 r WHERE r.inQueue = true ORDER BY r.patientNumber ASC")
    List<Row5> findAllByInQueueTrueOrderByPatientNumberAsc();

    Row5 findFirstByInQueueClinicTrueOrderByPatientNumberAsc();

    @Query("SELECT r FROM Row5 r WHERE r.inQueueClinic = true ORDER BY r.patientNumber ASC")
    List<Row5> findAllByInQueueClinicTrueOrderByPatientNumberAsc();


}