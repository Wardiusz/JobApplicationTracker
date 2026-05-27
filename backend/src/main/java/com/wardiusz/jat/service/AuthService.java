package com.wardiusz.jat.service;

import com.wardiusz.jat.dto.request.LoginRequest;
import com.wardiusz.jat.dto.request.RegisterRequest;

public interface AuthService {

    String login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    void activateUser(String email);

}
