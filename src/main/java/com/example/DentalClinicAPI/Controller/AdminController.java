package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.AvailabilityDTO;
import com.example.DentalClinicAPI.DTO.ClinicDTO;
import com.example.DentalClinicAPI.DTO.ServiceDTO;
import com.example.DentalClinicAPI.Repo.*;
import com.example.DentalClinicAPI.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "admin-methods")
public class AdminController {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final AvailabilityRepository availabilityRepository;

    @Operation(
            summary = "this method add new service in database",
            description = "getting service DTO and Builder create and save entity in database"
    )
    @PostMapping("/addService")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<String> addService(@RequestBody ServiceDTO serviceDTO){

        Service service = Service.builder()
                .title(serviceDTO.getTitle())
                .price(serviceDTO.getPrice())
                .description(serviceDTO.getDescription())
                .counts(serviceDTO.getCounts())
                .build();
        service = serviceRepository.save(service);

        if (serviceDTO.getAvailabilities() != null) {
            for (AvailabilityDTO availabilityDTO : serviceDTO.getAvailabilities()) {
                Availability availability = Availability.builder()
                        .service(service)
                        .date(availabilityDTO.getDate())
                        .time(availabilityDTO.getTime())
                        .isBooked(false)
                        .build();
                availabilityRepository.save(availability);
            }
        }

        return ResponseEntity.ok("Service with availabilities added successfully");

    }

//    @Operation(summary = "admin is adding clinic for monitoring and analyzing")
//    @PostMapping("/addClinic")
//    @PreAuthorize("hasRole('ADMIN')")
//    private void addClinic(@RequestBody ClinicDTO clinicDTO){
//        log.info("New row: " + clinicRepository.save(
//                Clinic.builder()
//                        .title(clinicDTO.getTitle())
//                        .services(clinicDTO.getServices())
//                        .awards(clinicDTO.getAwards())
//                        .dateBorn(clinicDTO.getDateBorn())
//                        .build())
//        );
//    }



    @Operation(summary = "admin is getting all users")
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    private List<User> getUsers(){
        return userRepository.findAll();
    }

    @Operation(summary = "Get all appointments for a specific user")
    @GetMapping("/AppointmentList")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Appointment> getAppointmentsForUser(@RequestParam String username) {
        return appointmentRepository.findAllByPatient(username);
    }



//    @Operation(
//            summary = "admin is getting service special patient",
//            description = "service getting by patient username"
//    )
//    @GetMapping("/servicePatient")
//    @PreAuthorize("hasRole('ADMIN')")
//    private List<Service> getServiceSpecialPatient(@RequestParam String username){
//        return serviceRepository.findAllByUsername(username);
//    }

    @Operation(summary = "admin updating service date")
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    private String updateService(@RequestParam Long id, @RequestBody Service service){
        if(!serviceRepository.existsById(service.getId())){
            return "Service not found";
        }
        return serviceRepository.save(service).toString();
    }

//    @PutMapping("/block")
//    @PreAuthorize("hasRole('ADMIN')")
//    private ResponseEntity<String> blockUser(@RequestParam Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(()-> new RuntimeException("user not found"));
//
//        user.setActive(false);
//        userRepository.save(user);
//
//        return ResponseEntity.ok("user has been blocked");
//    }
//
//    @PutMapping("/unblock")
//    @PreAuthorize("hasRole('ADMIN')")
//    private ResponseEntity<String> unblockUser(@RequestParam Long userId){
//        User user = userRepository.findById(userId)
//                .orElseThrow(()-> new RuntimeException("user not found"));
//
//        user.setActive(true);
//        userRepository.save(user);
//
//        return ResponseEntity.ok("user has been unblocked");
//    }


    @Operation(
            summary = "admin deleting service",
            description = "deleting by id service"
    )
    @DeleteMapping("/deleteService")
    @PreAuthorize("hasRole('ADMIN')")
    private ResponseEntity<String> deleteService(@RequestParam Long id){

        Service service = serviceRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Service not found"));

        List<Appointment> relatedAppointment = appointmentRepository.findAllByTitle(service.getTitle());
        if (!relatedAppointment.isEmpty()){
            appointmentRepository.deleteAll(relatedAppointment);
            log.info("Deleted related appointments for service: {}", service.getTitle());
        }

        serviceRepository.deleteById(id);
        log.info("Service deleted: {}", service.getTitle());
        return ResponseEntity.ok("Service and related appointments deleted successfully");
    }


    @Operation(
            summary = "admin deleting user",
            description = "deleting by id user"
    )
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@RequestBody Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Appointment> appointments = appointmentRepository.findAllByPatient(user.getUsername());
        appointmentRepository.deleteAll(appointments);

        userRepository.delete(user);
        return ResponseEntity.ok("User and related appointments deleted successfully");
    }


    // Получение всех услуг --
    // Изменение конкретной услуги --
    // Просмотр записи конкретного пациента
    // Просмотр всех пациентов --
    // Удаление из полного списка --
    // Удаление зарегистрированного пациента --


}
