package com.example.DentalClinicAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@Table(name = "clinics")
@Builder
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "services")
    private String services;

    @Column(name = "awards")
    private String awards;

    @Column(name = "dateBorn")
    private String dateBorn;

    public Clinic(){}
}
