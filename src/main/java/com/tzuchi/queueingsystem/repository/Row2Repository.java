package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.Row2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Row2Repository extends JpaRepository<Row2, String> {
    Row2 findFirstByInQueueTrueOrderByPriorityDescPatientNumberAsc();
}
