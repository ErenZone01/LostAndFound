package com.service_auth.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service_auth.auth.models.ApiResponse;
import com.service_auth.auth.models.User;
import com.service_auth.auth.models.ValidationGroups;
import com.service_auth.auth.services.AuthService;


@RestController
@RequestMapping("/api")
public class Authentification {
    final AuthService authService;
    public Authentification(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Validated(ValidationGroups.Login.class) @RequestBody User userLoginRequest) {
        return ResponseEntity.ok(authService.Login(userLoginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Validated(ValidationGroups.Register.class) @RequestBody User userRegistrationRequest) {
        return ResponseEntity.ok(authService.Register(userRegistrationRequest));
    }
}
