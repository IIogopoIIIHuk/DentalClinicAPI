package com.example.DentalClinicAPI.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ClinicDTO {
    private Long id;
    private String name;
    private String address;
    private String contact;
    private List<ServiceDTO> services;
}
