package com.tzuchi.queueingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "registration_station")
public class RegistrationStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registeredSequence;

    private String patientId;
    private Integer sectionNumber;
    private LocalDateTime registeredTime;
}