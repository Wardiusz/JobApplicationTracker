package com.wardiusz.jat.controller;

import com.wardiusz.jat.dto.RefreshTokenRequest;
import com.wardiusz.jat.model.entity.RefreshToken;
import com.wardiusz.jat.security.JwtTokenProvider;
import com.wardiusz.jat.dto.LoginRequest;
import com.wardiusz.jat.dto.RegisterRequest;
import com.wardiusz.jat.dto.JwtAuthResponse;
import com.wardiusz.jat.service.AuthService;
import com.wardiusz.jat.service.RefreshTokenService;
import jakarta.validation.Valid;
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

    private final JwtTokenProvider jwtTokenProvider;
    private AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest){
        String accessToken = authService.login(loginRequest);
        String refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername()).getToken();

        JwtAuthResponse loginResponse = new JwtAuthResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        String accessToken = authService.register(registerRequest);

        JwtAuthResponse registerResponse = new JwtAuthResponse();
        registerResponse.setAccessToken(accessToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        String newAccessToken = jwtTokenProvider.generateAccessToken(refreshToken.getUser().getUsername());

        JwtAuthResponse refreshResponse = new JwtAuthResponse();
        refreshResponse.setAccessToken(newAccessToken);
        refreshResponse.setRefreshToken(refreshToken.getToken());
        refreshResponse.setTokenType("Bearer");

        return ResponseEntity.ok(refreshResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
