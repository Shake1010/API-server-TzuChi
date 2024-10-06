package com.tzuchi.queueingsystem.repository;

import com.tzuchi.queueingsystem.entity.RegistrationStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationStationRepository extends JpaRepository<RegistrationStation, Long> {
}
