package com.devops.automation.service.controller;

import com.devops.automation.service.service.AuthService;
import com.devops.automation.service.model.AuthRequest;
import com.devops.automation.service.model.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.generateToken(authRequest.getUserName()));
    }

    @GetMapping("/valide")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        String userName = authService.validateToken(token);
        if (Objects.nonNull(userName)) {
            return ResponseEntity.ok("Token is valid");
        }
        return ResponseEntity.status(401).body("Invalid token");
    }
}
