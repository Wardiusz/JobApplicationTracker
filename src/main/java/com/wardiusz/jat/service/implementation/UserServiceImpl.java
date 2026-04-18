package com.wardiusz.jat.service.implementation;

import com.wardiusz.jat.exception.GlobalException;
import com.wardiusz.jat.mapper.UserMapper;
import com.wardiusz.jat.dto.request.CreateUserRequest;
import com.wardiusz.jat.dto.UserDTO;
import com.wardiusz.jat.entity.User;
import com.wardiusz.jat.repository.UserRepository;
import com.wardiusz.jat.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long id) {
        return UserMapper.toDTO(
                userRepository.findById(id)
                        .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, String.format("User with id %s not found.", id))));
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND,  String.format("User with name %s not found.", username)));

        if (!user.isEnabled()) {
            throw new DisabledException("Account is not verified.");
        }

        Set<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map((role) -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }

}