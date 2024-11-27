package com.example.DentalClinicAPI.Repo;

import com.example.DentalClinicAPI.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    List<Clinic> findByTitle(String title);
}
