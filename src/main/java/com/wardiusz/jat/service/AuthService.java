package com.wardiusz.jat.service;

import com.wardiusz.jat.dto.LoginRequest;
import com.wardiusz.jat.dto.RegisterRequest;

public interface AuthService {

    String login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);

}
