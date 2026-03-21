package com.wardiusz.jat.service;

import com.wardiusz.jat.model.request.CreateUserRequest;
import com.wardiusz.jat.model.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

        UserDTO getUserById(Long id);

        UserDTO createUser(CreateUserRequest request);

        void deleteUser(Long id);

}

