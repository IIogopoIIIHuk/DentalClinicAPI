package com.example.DentalClinicAPI.Repo;

import com.example.DentalClinicAPI.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByServiceId(Long serviceId);

    @Query("SELECT a FROM Availability a WHERE a.date = :date AND a.time = :time")
    List<Availability> findAllByDateAndTime(@Param("date") String date, @Param("time") String time);


}
