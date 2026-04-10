package com.wardiusz.jat.controller;

import com.wardiusz.jat.dto.request.OtpRequest;
import com.wardiusz.jat.entity.RefreshToken;
import com.wardiusz.jat.security.CookieUtil;
import com.wardiusz.jat.security.JwtTokenProvider;
import com.wardiusz.jat.dto.request.LoginRequest;
import com.wardiusz.jat.dto.request.RegisterRequest;
import com.wardiusz.jat.service.AuthService;
import com.wardiusz.jat.service.OtpTokenService;
import com.wardiusz.jat.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final OtpTokenService otpTokenService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    // POST /api/v1/auth/otp-resend
    @PostMapping("/otp-resend")
    public ResponseEntity<?> resendOtp(@RequestBody OtpRequest otpRequest) {
        return ResponseEntity.ok(otpTokenService.generateAndSendOTP(otpRequest.getEmail()));
    }

    // POST /api/v1/auth/otp-verify
    @PostMapping("/otp-verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest, HttpServletResponse response) {
        boolean otpToken = otpTokenService.validateOtp(otpRequest.getEmail(), otpRequest.getOtp());

        if (!otpToken) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

        authService.activateUser(otpRequest.getEmail());

        return ResponseEntity.ok().build();
    }

    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        String accessToken = authService.login(loginRequest);
        String refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername()).getToken();

        ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.noContent().build();
    }

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);

        otpTokenService.generateAndSendOTP(registerRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // POST /api/v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshTokenValue = extractCookie(request, "refresh_token");

        if (refreshTokenValue == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenValue);
        String newAccessToken = jwtTokenProvider.generateAccessToken(refreshToken.getUser().getUsername());

        ResponseCookie accessTokenCookie = cookieUtil.createAccessTokenCookie(newAccessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.noContent().build();
    }

    // POST /api/v1/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.clearAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.clearRefreshTokenCookie().toString());

        String accessToken = extractCookie(request, "access_token");
        if (accessToken != null) {
            String username = jwtTokenProvider.getUsername(accessToken);
            refreshTokenService.deleteByUser(username);
        }

        return ResponseEntity.noContent().build();
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
