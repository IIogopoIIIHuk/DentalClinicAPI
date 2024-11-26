package com.example.DentalClinicAPI.Repo;

import com.example.DentalClinicAPI.entity.Appointment;
import com.example.DentalClinicAPI.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllByPatient(String patient);

    List<Appointment> findAllByTitle(String title);
}
