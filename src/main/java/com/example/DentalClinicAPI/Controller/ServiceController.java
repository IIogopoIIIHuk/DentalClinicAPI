package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.ServiceDTO;
import com.example.DentalClinicAPI.Repo.ServiceRepository;
import com.example.DentalClinicAPI.Repo.UserRepository;
import com.example.DentalClinicAPI.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@Slf4j
@Tag(name = "main-methods")
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ServiceController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

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

    @GetMapping("/allServices")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    private List<Service> getAllSerivces(){
        return serviceRepository.findAll();
    }

    @GetMapping("/getById")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    private Service getServiceById(@RequestParam Long id){
         return serviceRepository.findServicesById(id).orElseThrow();
    }

    @PutMapping("/changeService")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeService(@RequestParam Long id,@RequestBody Service service){
        if(!serviceRepository.existsById(service.getId())){
            return "Service not found";
        }
        return serviceRepository.save(service).toString();
    }





}
