package com.example.DentalClinicAPI.DTO;

import lombok.Data;

@Data
public class AvailabilityDTO {
    private Long id;
    private Long serviceId;
    private String date;
    private String time;
    private boolean isBooked;
}
