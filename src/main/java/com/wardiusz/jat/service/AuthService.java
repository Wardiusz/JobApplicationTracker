package com.wardiusz.jat.service;

import com.wardiusz.jat.auth.dto.LoginDTO;
import com.wardiusz.jat.auth.dto.RegisterDTO;

public interface AuthService {
    String login(LoginDTO loginDTO);
    String register(RegisterDTO registerDTO);
}
