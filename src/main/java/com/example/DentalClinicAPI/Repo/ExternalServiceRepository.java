package com.example.DentalClinicAPI.Repo;


import com.example.DentalClinicAPI.entity.ExternalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExternalServiceRepository extends JpaRepository<ExternalService, Long> {


}
