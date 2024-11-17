//package com.example.DentalClinicAPI.Controller;
//
//import com.example.DentalClinicAPI.kafka.KafkaProducer;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.naming.ldap.Control;
//
//@RestController
//public class Controller {
//
//    private final KafkaProducer kafkaProducer;
//
//
//    public Controller(KafkaProducer kafkaProducer){
//        this.kafkaProducer = kafkaProducer;
//    }
//
//
//    @PostMapping("/kafka/send")
//    public String send(@RequestBody String message){
//        kafkaProducer.sendMessage(message);
//        return "Success";
//    }
//
//
//}
