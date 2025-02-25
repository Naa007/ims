package com.stepup.ims.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "reference", length = 255)
    private ReferenceType reference;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @ManyToOne
    @JoinColumn(name = "emp_id", referencedColumnName = "emp_id")
    private Employee employee;

    public enum ReferenceType {
        EMAIL_CONFIRMATION,
        PHONE_CONFIRMATION
    }
}
