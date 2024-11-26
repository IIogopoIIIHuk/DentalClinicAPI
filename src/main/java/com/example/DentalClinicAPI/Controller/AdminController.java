package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.ServiceDTO;
import com.example.DentalClinicAPI.Repo.AppointmentRepository;
import com.example.DentalClinicAPI.Repo.ServiceRepository;
import com.example.DentalClinicAPI.Repo.UserRepository;
import com.example.DentalClinicAPI.entity.Appointment;
import com.example.DentalClinicAPI.entity.Service;
import com.example.DentalClinicAPI.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "admin-methods")
public class AdminController {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;



    @Operation(
            summary = "this method add new service in database",
            description = "getting service DTO and Builder create and save entity in database"
    )
    @PostMapping("/addService")
    @PreAuthorize("hasRole('ADMIN')")
    private void addService(@RequestBody ServiceDTO serviceDTO){
        log.info("New row: " + serviceRepository.save(
                Service.builder()
                        .title(serviceDTO.getTitle())
                        .dataService(serviceDTO.getDataService())
                        .price(serviceDTO.getPrice())
                        .description(serviceDTO.getDescription())
                        .counts(serviceDTO.getCounts())
                        .build())
        );
    }

    @Operation(
            summary = "admin is getting all services"
    )
    @GetMapping("/allServices")
//    @PreAuthorize("hasRole('ADMIN')")  под вопросом роль
    private List<Service> getAllService(){
        return serviceRepository.findAll();
    }

    @Operation(
            summary = "admin is getting all users"
    )
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    private List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/AppointmentList")
    @PreAuthorize("hasRole('ADMIN')")
    private List<Appointment> getAppointment(){
        return appointmentRepository.findAll();
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

    @Operation(
            summary = "admin updating service date"
    )
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    private String updateService(@RequestParam Long id, @RequestBody Service service){
        if(!serviceRepository.existsById(service.getId())){
            return "Service not found";
        }
        return serviceRepository.save(service).toString();
    }

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
    private void deleteUser(@RequestBody Long id){
        userRepository.deleteById(id);
    }

    // Получение всех услуг --
    // Изменение конкретной услуги --
    // Просмотр записи конкретного пациента
    // Просмотр всех пациентов --
    // Удаление из полного списка --
    // Удаление зарегистрированного пациента --


}
