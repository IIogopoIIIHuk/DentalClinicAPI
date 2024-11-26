package com.example.DentalClinicAPI.DTO;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
    private String email;
}
