package com.wardiusz.jat.dto;

import com.wardiusz.jat.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    String username;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String password;

    UserType userType;

}
