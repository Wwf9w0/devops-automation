package com.devops.automation.service.service;

import com.devops.automation.service.model.AuthResponse;
import com.devops.automation.service.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    public AuthResponse generateToken(String userName) {
        String token = jwtUtil.generateToken(userName);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public String validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
