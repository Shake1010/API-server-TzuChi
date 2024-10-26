package com.tzuchi.queueingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "row8")
public class Row8 {
    @Id
    private String patientId;

    private Integer patientNumber;  // Add this line
    private LocalDateTime registeredTime;
    private Boolean inQueue;
    private Boolean inQueueClinic;
}