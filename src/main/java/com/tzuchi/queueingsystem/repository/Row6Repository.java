package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row5;
import com.tzuchi.queueingsystem.entity.Row6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Row6Repository extends JpaRepository<Row6, String> {
    Row6 findFirstByInQueueTrueOrderByPatientNumberAsc();

    @Query("SELECT MAX(r.patientNumber) FROM Row6 r")
    Integer findMaxPatientNumber();



    @Query("SELECT r FROM Row6 r ORDER BY r.patientNumber ASC")
    List<Row6> findAllSortedByPatientNumber();

    @Query("SELECT r FROM Row6 r WHERE r.inQueue = true ORDER BY r.patientNumber ASC")
    List<Row6> findAllByInQueueTrueOrderByPatientNumberAsc();

    Row6 findFirstByInQueueClinicTrueOrderByPatientNumberAsc();

    @Query("SELECT r FROM Row6 r WHERE r.inQueueClinic = true ORDER BY r.patientNumber ASC")
    List<Row6> findAllByInQueueClinicTrueOrderByPatientNumberAsc();

}
