package com.example.DentalClinicAPI.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.DentalClinicAPI.entity.Service;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
//    List<Service> findOwnAppointment(String username);

    List<Service> findServiceByTitle(String title);

//    List<Service> findAllByUsername(String username);
}
