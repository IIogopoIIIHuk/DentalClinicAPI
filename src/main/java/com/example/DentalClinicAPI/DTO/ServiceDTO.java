package com.example.DentalClinicAPI.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ServiceDTO {
    private String title;
    private String dataService;
    private int price;
    private String description;
    private int counts;

}
