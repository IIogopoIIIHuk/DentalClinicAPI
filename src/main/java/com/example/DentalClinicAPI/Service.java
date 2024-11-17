package com.example.DentalClinicAPI;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.core.SpringVersion;
import org.springframework.web.bind.annotation.PutMapping;


@Data
@Entity
@Builder
@AllArgsConstructor
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "dateService")
    private String dataService;

    @Column(name = "price")
    private int price;

    @Column(name = "description")
    private String description;

    @Column(name = "counts")
    private int counts;


    public Service( String title, String dataService, int price, String description, int counts){
        this.title = title;
        this.dataService = dataService;
        this.price = price;
        this. description = description;
        this.counts = counts;
    }

    public Service(){}

    @Override
    public String toString(){
        return "Book{" +
                "id= " + id +
                ", isbn= " + title +
                ", title= " + dataService +
                ", description= " + price +
                ", genre= " + description +
                ", author= " + counts +
                '}';
    }

}
