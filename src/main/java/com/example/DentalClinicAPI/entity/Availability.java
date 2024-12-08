package com.example.DentalClinicAPI.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "availabilities")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    @JsonBackReference
    private Service service;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "isBooked", nullable = false)
    private boolean isBooked = false;


}
