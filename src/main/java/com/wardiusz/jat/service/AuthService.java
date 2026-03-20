package com.wardiusz.jat.service;

import com.wardiusz.jat.security.dto.LoginDTO;
import com.wardiusz.jat.security.dto.RegisterDTO;

public interface AuthService {
    String login(LoginDTO loginDTO);
    String register(RegisterDTO registerDTO);
}
