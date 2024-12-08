package com.example.DentalClinicAPI.Controller;

import com.example.DentalClinicAPI.DTO.AvailabilityDTO;
import com.example.DentalClinicAPI.DTO.ServiceDTO;
import com.example.DentalClinicAPI.Repo.*;
import com.example.DentalClinicAPI.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Tag(name = "user-methods")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ExternalClinicRepository externalClinicRepository;
    private final AvailabilityRepository availabilityRepository;


    @Operation(summary = "user is getting all services")
    @GetMapping("/allServices")
//    @PreAuthorize("hasRole('USER')")
    private List<ServiceDTO> getAllService(){
        return serviceRepository.findAll().stream()
                .map(service -> {
                    ServiceDTO serviceDTO = new ServiceDTO();
                    serviceDTO.setId(service.getId());
                    serviceDTO.setTitle(service.getTitle());
                    serviceDTO.setPrice(service.getPrice());
                    serviceDTO.setDescription(service.getDescription());
                    serviceDTO.setCounts(service.getCounts());
                    serviceDTO.setAvailabilities(service.getAvailabilities().stream()
                            .map(availability -> {
                                AvailabilityDTO availabilityDTO = new AvailabilityDTO();
                                availabilityDTO.setId(availability.getId());
                                availabilityDTO.setServiceId(availability.getService().getId());
                                availabilityDTO.setDate(availability.getDate());
                                availabilityDTO.setTime(availability.getTime());
                                return availabilityDTO;
                            }).collect(Collectors.toList()));
                    return serviceDTO;
                }).collect(Collectors.toList());
    }


    @Operation(summary = "Get current user details")
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCurrentUser() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
        ));
    }


    @Operation(summary = "Upload user avatar")
    @PostMapping("/uploadAvatar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUserName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Папка для загрузки
            String uploadDir = "uploads/avatars/";
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;

            // Сохраняем файл
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            file.transferTo(new File(filePath));

            // Обновляем пользователя
            user.setAvatar("/uploads/avatars/" + fileName);
            userRepository.save(user);

            // Возвращаем путь к новому аватару
            return ResponseEntity.ok(Map.of("avatarUrl", user.getAvatar()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload avatar");
        }
    }

    @Operation(summary = "Get service details with available slots")
    @GetMapping("/service/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ServiceDTO> getServiceDetails(@PathVariable Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(service.getId());
        serviceDTO.setTitle(service.getTitle());
        serviceDTO.setDescription(service.getDescription());
        serviceDTO.setPrice(service.getPrice());
        serviceDTO.setCounts(service.getCounts());
        serviceDTO.setAvailabilities(
                service.getAvailabilities().stream()
                        .map(availability -> {
                            AvailabilityDTO availabilityDTO = new AvailabilityDTO();
                            availabilityDTO.setId(availability.getId());
                            availabilityDTO.setDate(availability.getDate());
                            availabilityDTO.setTime(availability.getTime());
                            availabilityDTO.setBooked(availability.isBooked());
                            return availabilityDTO;
                        }).collect(Collectors.toList())
        );
        return ResponseEntity.ok(serviceDTO);
    }



    @Operation(summary = "User books a specific time slot for a service")
    @PostMapping("/makeAnAppointment/{availabilityId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> makeAnAppointment(@PathVariable Long availabilityId) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current user: {}", currentUser);

        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (availability.isBooked()) {
            return ResponseEntity.badRequest().body("This slot is already booked.");
        }

        Appointment appointment = Appointment.builder()
                .title(availability.getService().getTitle())
                .description(availability.getService().getDescription())
                .price(availability.getService().getPrice())
                .patient(currentUser)
                .date(availability.getDate())
                .time(availability.getTime())
                .build();

        appointmentRepository.save(appointment);

        availability.setBooked(true);
        availabilityRepository.save(availability);

        log.info("Appointment successfully added: {}", appointment);
        return ResponseEntity.ok("Appointment successfully added.");
    }

    @Operation(summary = "Get user's appointments")
    @GetMapping("/myAppointment")
    @PreAuthorize("hasRole('USER')")
    public List<Appointment> getUserAppointments(@RequestParam(required = false) String sort) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Appointment> appointments = appointmentRepository.findAllByPatient(currentUser);

        if ("date".equalsIgnoreCase(sort)) {
            appointments = appointments.stream()
                    .sorted(Comparator.comparing((Appointment app) -> LocalDate.parse(app.getDate()))
                            .thenComparing(app -> LocalTime.parse(app.getTime())))
                    .collect(Collectors.toList());
        }
        return appointments;
    }


    @Operation(summary = "Get available time slots for a service")
    @GetMapping("/service/{id}/available-slots")
    @PreAuthorize("hasRole('USER')")
    public List<AvailabilityDTO> getAvailableTimeSlots(@PathVariable Long id) {
        return availabilityRepository.findByServiceId(id).stream()
                .filter(availability -> !availability.isBooked()) // Только доступные слоты
                .map(availability -> {
                    AvailabilityDTO availabilityDTO = new AvailabilityDTO();
                    availabilityDTO.setId(availability.getId());
                    availabilityDTO.setDate(availability.getDate());
                    availabilityDTO.setTime(availability.getTime());
                    return availabilityDTO;
                })
                .collect(Collectors.toList());
    }

    @Operation(summary = "Cancel appointment and free time slot")
    @DeleteMapping("/cancelAppointment/{appointmentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        List<Availability> availabilities = availabilityRepository.findAllByDateAndTime(
                appointment.getDate(), appointment.getTime());

        if (!availabilities.isEmpty()) {
            for (Availability availability : availabilities) {
                availability.setBooked(false);
                availabilityRepository.save(availability);
            }
        }

        appointmentRepository.delete(appointment);
        log.info("Appointment canceled: {}", appointment);
        return ResponseEntity.ok("Appointment canceled and time slot freed.");
    }


    @Operation(
            summary = "user is searching service by title"
    )
    @GetMapping("/searchService")
    @PreAuthorize("hasRole('USER')")
    private List<Service> getServiceByTitle(@RequestParam String title){
        return serviceRepository.findServiceByTitle(title);
    }

    @Operation(summary = "Delete appointment and free associated time slot")
    @DeleteMapping("/deleteAppointment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteAppointment(@RequestParam Long id) {
        log.info("Request to delete appointment with ID: {}", id);

        // Найти запись
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        log.info("Appointment found: {}", appointment);

        // Найти связанные слоты (может быть несколько, если слоты дублируются)
        List<Availability> availabilities = availabilityRepository.findAllByDateAndTime(
                appointment.getDate(), appointment.getTime()
        );

        log.info("Associated availabilities found: {}", availabilities);

        // Освобождение всех связанных слотов
        if (!availabilities.isEmpty()) {
            for (Availability availability : availabilities) {
                availability.setBooked(false);
                availabilityRepository.save(availability);
                log.info("Availability freed: {}", availability);
            }
        }

        // Удаление записи
        appointmentRepository.deleteById(id);
        log.info("Appointment deleted successfully: {}", id);

        return ResponseEntity.ok("Appointment successfully canceled.");
    }



}
