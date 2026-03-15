package com.wardiusz.jat.service;

import com.wardiusz.jat.model.dto.CreateUserRequest;
import com.wardiusz.jat.model.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDTO getUserById(Long id) {
        return null;
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}