package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.ExternalClinicDTO;
import com.example.DentalClinicAPI.DTO.ExternalServiceDTO;
import com.example.DentalClinicAPI.Repo.ExternalClinicRepository;
import com.example.DentalClinicAPI.Repo.ExternalServiceRepository;
import com.example.DentalClinicAPI.entity.ExternalClinic;
import com.example.DentalClinicAPI.entity.ExternalService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external-clinics")
public class ExternalClinicController {

    private final ExternalClinicRepository clinicRepository;
    private final ExternalServiceRepository externalServiceRepository;


    private Map<String, Double> getCoordinates(String address) {
        String apiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + address;
        RestTemplate restTemplate = new RestTemplate();
        try {
            Map[] response = restTemplate.getForObject(apiUrl, Map[].class);
            if (response != null && response.length > 0) {
                double lat = Double.parseDouble(response[0].get("lat").toString());
                double lon = Double.parseDouble(response[0].get("lon").toString());
                return Map.of("lat", lat, "lon", lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of("lat", 0.0, "lon", 0.0); // Координаты по умолчанию
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addClinic(@RequestBody ExternalClinicDTO clinicDTO) {
        Map<String, Double> coordinates = getCoordinates(clinicDTO.getAddress());
        ExternalClinic clinic = ExternalClinic.builder()
                .name(clinicDTO.getName())
                .address(clinicDTO.getAddress())
                .contact(clinicDTO.getContact())
                .latitude(coordinates.get("lat").toString())
                .longitude(coordinates.get("lon").toString())
                .build();

        if (clinicDTO.getServices() != null && !clinicDTO.getServices().isEmpty()) {
            List<ExternalService> services = clinicDTO.getServices().stream()
                    .map(serviceDTO -> ExternalService.builder()
                            .title(serviceDTO.getTitle())
                            .price(serviceDTO.getPrice())
                            .description(serviceDTO.getDescription())
                            .clinic(clinic)
                            .build())
                    .collect(Collectors.toList());
            clinic.setServices(services);
        }

        clinicRepository.save(clinic);
        return ResponseEntity.ok("External clinic and services added successfully");
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteClinic(@PathVariable Long id){
        ExternalClinic externalClinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        clinicRepository.deleteById(id);
        return ResponseEntity.ok("Clinic successfully deleted");
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateClinic(@PathVariable Long id, @RequestBody ExternalClinicDTO clinicDTO) {
        ExternalClinic externalClinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));

        Map<String, Double> coordinates = getCoordinates(clinicDTO.getAddress());
        externalClinic.setName(clinicDTO.getName());
        externalClinic.setAddress(clinicDTO.getAddress());
        externalClinic.setContact(clinicDTO.getContact());
        externalClinic.setLatitude(coordinates.get("lat").toString());
        externalClinic.setLongitude(coordinates.get("lon").toString());

        if (clinicDTO.getServices() != null && !clinicDTO.getServices().isEmpty()) {
            externalClinic.getServices().clear();
            clinicDTO.getServices().forEach(serviceDTO -> {
                ExternalService service = ExternalService.builder()
                        .title(serviceDTO.getTitle())
                        .price(serviceDTO.getPrice())
                        .description(serviceDTO.getDescription())
                        .clinic(externalClinic)
                        .build();
                externalClinic.getServices().add(service);
            });
        }
        clinicRepository.save(externalClinic);
        return ResponseEntity.ok("Clinic successfully updated");
    }


    @GetMapping("/{id}")
    public ResponseEntity<ExternalClinicDTO> getClinicById(@PathVariable Long id) {
        ExternalClinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиника не найдена"));

        ExternalClinicDTO dto = new ExternalClinicDTO();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setAddress(clinic.getAddress());
        dto.setContact(clinic.getContact());
        dto.setLatitude(clinic.getLatitude());
        dto.setLongitude(clinic.getLongitude());
        dto.setServices(clinic.getServices().stream()
                .map(service -> {
                    ExternalServiceDTO serviceDTO = new ExternalServiceDTO();
                    serviceDTO.setId(service.getId());
                    serviceDTO.setTitle(service.getTitle());
                    serviceDTO.setPrice(service.getPrice());
                    serviceDTO.setDescription(service.getDescription());
                    return serviceDTO;
                })
                .collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/all")
    public List<ExternalClinicDTO> getAllClinics() {
        return clinicRepository.findAll().stream()
                .map(clinic -> {
                    ExternalClinicDTO dto = new ExternalClinicDTO();
                    dto.setId(clinic.getId());
                    dto.setName(clinic.getName());
                    dto.setAddress(clinic.getAddress());
                    dto.setContact(clinic.getContact());
                    dto.setLongitude(clinic.getLongitude());
                    dto.setLatitude(clinic.getLatitude());
                    dto.setServices(clinic.getServices().stream()
                            .map(service -> {
                                ExternalServiceDTO serviceDTO = new ExternalServiceDTO();
                                serviceDTO.setId(service.getId());
                                serviceDTO.setTitle(service.getTitle());
                                serviceDTO.setPrice(service.getPrice());
                                serviceDTO.setDescription(service.getDescription());
                                return serviceDTO;
                            })
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/analytics")
    public List<ExternalClinicDTO> getAnalytics(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {

        List<ExternalClinic> clinics = clinicRepository.findAll();

        // Фильтрация по услуге
        if (serviceName != null && !serviceName.isEmpty()) {
            clinics = clinics.stream()
                    .filter(clinic -> clinic.getServices().stream()
                            .anyMatch(service -> service.getTitle().toLowerCase().contains(serviceName.toLowerCase())))
                    .collect(Collectors.toList());
        }

        // Фильтрация по цене
        if (minPrice != null || maxPrice != null) {
            clinics = clinics.stream()
                    .filter(clinic -> clinic.getServices().stream()
                            .anyMatch(service -> {
                                double price = service.getPrice();
                                return (minPrice == null || price >= minPrice) && (maxPrice == null || price <= maxPrice);
                            }))
                    .collect(Collectors.toList());
        }

        // Сортировка
        if (sortBy != null) {
            Comparator<ExternalClinic> comparator = switch (sortBy) {
                case "name" -> Comparator.comparing(ExternalClinic::getName);
                case "price" -> Comparator.comparingDouble(clinic -> clinic.getServices().stream()
                        .mapToDouble(ExternalService::getPrice).min().orElse(0));
                default -> Comparator.comparing(ExternalClinic::getId);
            };

            if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
            }

            clinics.sort(comparator);
        }

        return clinics.stream()
                .map(clinic -> {
                    ExternalClinicDTO dto = new ExternalClinicDTO();
                    dto.setId(clinic.getId());
                    dto.setName(clinic.getName());
                    dto.setAddress(clinic.getAddress());
                    dto.setContact(clinic.getContact());
                    dto.setServices(clinic.getServices().stream()
                            .map(service -> {
                                ExternalServiceDTO serviceDTO = new ExternalServiceDTO();
                                serviceDTO.setId(service.getId());
                                serviceDTO.setTitle(service.getTitle());
                                serviceDTO.setPrice(service.getPrice());
                                serviceDTO.setDescription(service.getDescription());
                                return serviceDTO;
                            })
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
