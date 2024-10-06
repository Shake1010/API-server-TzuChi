package com.tzuchi.queueingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "row2")
public class Row2 {
    @Id
    private String patientId;

    private Character patientCategory;
    private Integer patientNumber;
    private Integer sectionNumber;
    private Boolean inQueue;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    public enum Priority {
        LOW, MID, HIGH
    }
}