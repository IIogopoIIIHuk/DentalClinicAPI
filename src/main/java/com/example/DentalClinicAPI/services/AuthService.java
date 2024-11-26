package com.example.DentalClinicAPI.services;

import com.example.DentalClinicAPI.DTO.JwtRequest;
import com.example.DentalClinicAPI.DTO.JwtResponse;
import com.example.DentalClinicAPI.DTO.RegistrationUserDTO;
import com.example.DentalClinicAPI.DTO.UserDTO;
import com.example.DentalClinicAPI.entity.User;
import com.example.DentalClinicAPI.exceptions.AppError;
import com.example.DentalClinicAPI.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> createNewUser(RegistrationUserDTO registrationUserDTO) {
        if (userService.findByUsername(registrationUserDTO.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDTO);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getUsername(), user.getEmail()));
    }
}
