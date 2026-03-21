package com.wardiusz.jat.controller;

import com.wardiusz.jat.security.dto.LoginRequest;
import com.wardiusz.jat.security.dto.RegisterRequest;
import com.wardiusz.jat.security.dto.JwtAuthResponse;
import com.wardiusz.jat.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest){
        String token = authService.login(loginRequest);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        String token = authService.register(registerRequest);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setAccessToken(token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
