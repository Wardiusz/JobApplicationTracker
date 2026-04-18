package com.wardiusz.jat.service.implementation;

import com.wardiusz.jat.exception.GlobalException;
import com.wardiusz.jat.security.JwtTokenProvider;
import com.wardiusz.jat.dto.request.LoginRequest;
import com.wardiusz.jat.dto.request.RegisterRequest;
import com.wardiusz.jat.enums.UserType;
import com.wardiusz.jat.entity.User;
import com.wardiusz.jat.repository.UserRepository;
import com.wardiusz.jat.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateAccessToken(authentication.getName());
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new GlobalException(HttpStatus.CONFLICT, "Username is taken."); // 409 error
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new GlobalException(HttpStatus.CONFLICT, "Email is taken."); // 409 error
        }

        if (registerRequest.getPassword().length() < 8) {
            throw new GlobalException(HttpStatus.LENGTH_REQUIRED, "Password too short. Need to be at least 8 characters long."); // 411 error
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .type(UserType.USER)
                .jobs(new ArrayList<>())
                .build();

        userRepository.save(user);
    }

    @Override
    public void activateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);

        userRepository.save(user);
    }
}
