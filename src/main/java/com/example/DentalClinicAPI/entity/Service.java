package com.example.DentalClinicAPI.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "counts", nullable = false)
    private int counts;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Availability> availabilities; // Список доступных мест

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", counts=" + counts +
                '}';
    }
}
