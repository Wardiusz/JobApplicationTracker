package com.wardiusz.jat.mapper;

import com.wardiusz.jat.enums.UserType;
import com.wardiusz.jat.dto.request.CreateUserRequest;
import com.wardiusz.jat.dto.UserDTO;
import com.wardiusz.jat.entity.User;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User toEntity(CreateUserRequest request) {
        if (request == null) return null;
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .type(UserType.USER)
                .build();
    }

    public void updateEntity(User user, UserDTO dto) {
        if (user == null || dto == null) return;
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setType(UserType.USER);
    }

    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .toList();
    }

}
