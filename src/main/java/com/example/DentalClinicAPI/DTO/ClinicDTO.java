package com.example.DentalClinicAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClinicDTO {

    private String title;
    private String services;
    private String awards;
    private String dateBorn;

}
