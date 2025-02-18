package com.stepup.ims.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 100)
    private String name;
    @PastOrPresent
    private LocalDate dateIssued;
    @FutureOrPresent
    private LocalDate expiryDate;
    @NotBlank
    private String issuer;
    @ManyToOne
    @JoinColumn(name = "inspector_id", nullable = false)
    private Inspector inspector;
}
