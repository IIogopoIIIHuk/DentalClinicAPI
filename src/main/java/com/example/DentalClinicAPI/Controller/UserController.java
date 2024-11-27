package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.ServiceDTO;
import com.example.DentalClinicAPI.Repo.AppointmentRepository;
import com.example.DentalClinicAPI.Repo.ClinicRepository;
import com.example.DentalClinicAPI.Repo.ServiceRepository;
import com.example.DentalClinicAPI.Repo.UserRepository;
import com.example.DentalClinicAPI.entity.Appointment;
import com.example.DentalClinicAPI.entity.Clinic;
import com.example.DentalClinicAPI.entity.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Tag(name = "user-methods")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;


    @Operation(
            summary = "user is getting all services"
    )
    @GetMapping("/allServices")
    @PreAuthorize("hasRole('USER')")
    private List<Service> getAllService(){
        return serviceRepository.findAll();
    }


    @Operation(
            summary = "user is getting all clinic for monitoring and analyzing"
    )
    @GetMapping("/allClinic")
    @PreAuthorize("hasRole('USER')")
    private List<Clinic> getAllClinic(){
        return clinicRepository.findAll();
    }

//    @Operation(
//            summary = "user is searching clinic by title"
//    )
//    @GetMapping("/searchService")
//    @PreAuthorize("hasRole('USER')")
//    private List<Clinic> getClinicByTitle(@RequestParam String title){
//        return clinicRepository.findByTitle(title);
//    }

    @Operation(
            summary = "user make an appointment to service"
    )
    @PostMapping("/makeAnAppointment/{id}")
    @PreAuthorize("hasRole('USER')")
    private ResponseEntity<String> makeAnAppointment(@PathVariable Long id){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("current user: {}", currentUser);

        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Appointment appointment = Appointment.builder()
                .title(service.getTitle())
                .description(service.getDescription())
                .dataService(service.getDataService())
                .price(service.getPrice())
                .patient(currentUser)
                .build();

        appointmentRepository.save(appointment);

        log.info("appointment successfully added: {}", appointment);
        return ResponseEntity.ok("appointment successfully added");
    }

    @Operation(
            summary = "user is getting own appointment"
    )
    @GetMapping("/myAppointment")
    @PreAuthorize("hasRole('USER')")
    private List<Appointment> getOwnAppointment(){
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return appointmentRepository.findAllByPatient(currentUser);
    }

    @Operation(
            summary = "user is searching service by title"
    )
    @GetMapping("/searchService")
    @PreAuthorize("hasRole('USER')")
    private List<Service> getServiceByTitle(@RequestParam String title){
        return serviceRepository.findServiceByTitle(title);
    }

    @Operation(
            summary = "user is deleting his appointment"
    )
    @DeleteMapping("/deleteAppointment")
    @PreAuthorize("hasRole('USER')")
    private void deleteAppointment(@RequestParam Long id){
        appointmentRepository.deleteById(id);
    }

    // Запись на прием --
    // Отмена записи--

}
