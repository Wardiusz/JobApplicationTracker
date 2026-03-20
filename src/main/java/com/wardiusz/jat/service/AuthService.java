package com.wardiusz.jat.service;

import com.wardiusz.jat.security.dto.LoginRequest;
import com.wardiusz.jat.security.dto.RegisterRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
}
