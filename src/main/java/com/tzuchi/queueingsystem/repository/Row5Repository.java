package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row5;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Row5Repository extends JpaRepository<Row5, String> {
    Row5 findFirstByInQueueTrueOrderByPatientNumberAsc();
}