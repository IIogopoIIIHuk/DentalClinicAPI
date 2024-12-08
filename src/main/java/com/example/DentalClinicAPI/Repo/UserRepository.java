package com.example.DentalClinicAPI.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.DentalClinicAPI.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}
