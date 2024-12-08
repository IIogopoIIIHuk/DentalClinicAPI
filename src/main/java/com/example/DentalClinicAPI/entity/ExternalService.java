package com.example.DentalClinicAPI.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "external_services")
public class ExternalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private ExternalClinic clinic;
}
