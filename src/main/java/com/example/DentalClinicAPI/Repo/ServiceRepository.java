package com.example.DentalClinicAPI.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.DentalClinicAPI.Service;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findServicesById(Long id);
}
