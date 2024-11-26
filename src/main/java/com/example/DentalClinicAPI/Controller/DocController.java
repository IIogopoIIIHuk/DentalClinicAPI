//package com.example.DentalClinicAPI.Controller;
//
//import com.example.DentalClinicAPI.Repo.DocRepository;
//import com.example.DentalClinicAPI.entity.Doctor;
//import lombok.AllArgsConstructor;
//import org.apache.kafka.common.protocol.types.Field;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.print.Doc;
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/doc")
//public class DocController {
//
//    private final DocRepository docRepository;
//
//    @GetMapping("/all")
//    public List<Doctor> getAllDocsFromDB(@RequestParam String name){
//        return docRepository.findAll();
//    }
//
//    @GetMapping
//    public ResponseEntity<Doctor> getDocByName(@RequestParam String name){
//
//        var doc = docRepository.findByName(name);
//        if (doc==null){
//            return ResponseEntity
//                    .status(HttpStatusCode.valueOf(404))
//                    .build();
//        }
//
//        return new ResponseEntity<>(doc, HttpStatusCode.valueOf(200));
//    }
//
//    @PostMapping
//    public Doctor putDocIntoBD(@RequestBody Doctor doctor){
//        doctor.setId(UUID.randomUUID());
//        return docRepository.save(doctor);
//    }
//
//
//
//}
