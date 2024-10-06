package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row6;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Row6Repository extends JpaRepository<Row6, String> {
    Row6 findFirstByInQueueTrueOrderByPatientNumberAsc();
}