package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "StatusTable")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId; // Primary Key

    @Column(nullable = false, unique = true, length = 100)
    private String statusName; // Name of the lifecycle status

    public Status() {
    }

    public Status(String statusName) {
        this.statusName = statusName;
    }
}