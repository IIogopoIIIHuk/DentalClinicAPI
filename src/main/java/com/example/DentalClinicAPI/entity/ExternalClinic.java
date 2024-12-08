package com.example.DentalClinicAPI.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "external_clinics")
public class ExternalClinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact", nullable = false)
    private String contact;

    private String latitude;

    private String longitude;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExternalService> services;
}
