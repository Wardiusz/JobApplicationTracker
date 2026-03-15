package com.wardiusz.jat.service;

import com.wardiusz.jat.model.dto.CreateUserRequest;
import com.wardiusz.jat.model.dto.UserDTO;
import org.springframework.stereotype.Service;

public interface UserService {
        UserDTO getUserById(Long id);
        UserDTO createUser(CreateUserRequest request);
        void deleteUser(Long id);}

