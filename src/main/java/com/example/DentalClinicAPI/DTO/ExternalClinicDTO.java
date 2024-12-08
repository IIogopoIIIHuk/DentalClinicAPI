package com.example.DentalClinicAPI.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ExternalClinicDTO {
    private Long id;
    private String name;
    private String address;
    private String contact;
    private String latitude;
    private String longitude;
    private List<ExternalServiceDTO> services;
}
