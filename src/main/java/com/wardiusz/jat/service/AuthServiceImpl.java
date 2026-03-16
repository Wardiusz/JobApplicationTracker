package com.wardiusz.jat.service;

import com.wardiusz.jat.auth.JwtTokenProvider;
import com.wardiusz.jat.auth.dto.LoginDTO;
import com.wardiusz.jat.auth.dto.RegisterDTO;
import com.wardiusz.jat.enums.UserType;
import com.wardiusz.jat.model.entity.User;
import com.wardiusz.jat.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static com.wardiusz.jat.config.SecurityConfig.passwordEncoder;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public String register(RegisterDTO registerDTO) {
        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username zajęty");
        }

        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email zajęty");
        }

        User user = User.builder()
                .email(registerDTO.getEmail())
                .username(registerDTO.getUsername())
                .password(passwordEncoder().encode(registerDTO.getPassword()))
                .type(UserType.USER)
                .jobs(new ArrayList<>())
                .build();

        userRepository.save(user);
        return jwtTokenProvider.generateToken(user);
    }
}
