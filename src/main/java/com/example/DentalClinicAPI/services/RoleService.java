package com.example.DentalClinicAPI.services;

import com.example.DentalClinicAPI.Repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.DentalClinicAPI.entity.Role;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NoSuchElementException("Role not found in database"));
    }
}
