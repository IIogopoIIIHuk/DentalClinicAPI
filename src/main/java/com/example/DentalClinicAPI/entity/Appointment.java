package com.example.DentalClinicAPI.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Table(name = "appointments")
@Entity
@Builder
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "price")
    private int price;

    @Column(name = "patient")
    private String patient;

    public Appointment() {}

    @Override
    public String toString(){
        return "Appointment{" +
                "id= " + id +
                ", title= " + title +
                ", date= " + date +
                ", time= " + time +
                ", price= " + price +
                ", description= " + description +
                ", patient= " + patient +
                '}';
    }
}
