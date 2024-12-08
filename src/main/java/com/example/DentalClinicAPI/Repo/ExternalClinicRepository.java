package com.example.DentalClinicAPI.Repo;

import com.example.DentalClinicAPI.entity.ExternalClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalClinicRepository extends JpaRepository<ExternalClinic, Long> {
}
