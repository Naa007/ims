package com.stepup.ims.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees") // Explicitly define the table name
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Explicitly define the column name
    private Long id;

    @Column(name = "emp_name", nullable = false) // Explicitly define the column name and constraints
    private String empName;

    @Column(name = "emp_id", unique = true, nullable = false)
    private String empId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "active", nullable = false)
    private String active;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}