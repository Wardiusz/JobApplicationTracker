package com.wardiusz.jat.service;

import com.wardiusz.jat.dto.CreateUserRequest;
import com.wardiusz.jat.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

        UserDTO getUserById(Long id);

        UserDTO createUser(CreateUserRequest request);

        void deleteUser(Long id);

}

