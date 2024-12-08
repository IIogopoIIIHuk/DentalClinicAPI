package com.example.DentalClinicAPI.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/page")
public class PageController{

    @GetMapping("/aut")
    public Map<String, Object> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()){
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

            return Map.of(
                    "username", authentication.getName(),
                    "roles", authentication.getAuthorities(),
                    "isAdmin", isAdmin,
                    "isUser", isUser
            );
        }

        return Map.of("authentication", false);
    }

}