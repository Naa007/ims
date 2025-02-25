package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employees") // Explicitly define the table name
public class Employee {

    @Id
    @Column(name = "emp_id", unique = true, nullable = false) // Using empId as primary key
    private String empId;

    @Column(name = "emp_name", nullable = false) // Explicitly define the column name and constraints
    private String empName;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "active", nullable = false)
    private String active;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

}