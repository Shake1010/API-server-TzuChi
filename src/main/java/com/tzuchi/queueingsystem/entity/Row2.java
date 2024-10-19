package com.tzuchi.queueingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "row2")
public class Row2 {
    @Id
    private String patientId;

    private Integer patientNumber;
    private Character patientCategory;
    private boolean inQueue;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "registered_time")
    private LocalDateTime registeredTime;

    @Column(name = "section_number")
    private Integer sectionNumber;

    public enum Priority {
        HIGH, MID, LOW, UNKNOWN
    }

    public void setPriorityFromString(String priorityString) {
        try {
            this.priority = Priority.valueOf(priorityString.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            this.priority = Priority.UNKNOWN;
        }
    }

    public String getPriorityAsString() {
        return priority != null ? priority.name() : "UNKNOWN";
    }
}