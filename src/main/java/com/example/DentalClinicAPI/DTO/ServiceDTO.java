package com.example.DentalClinicAPI.DTO;

import com.example.DentalClinicAPI.entity.Service;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceDTO {
    private Long id;
    private String title;
    private int price;
    private String description;
    private int counts;
    private List<AvailabilityDTO> availabilities;

}
