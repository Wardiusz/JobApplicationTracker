package com.wardiusz.jat.dto.request;

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

    @NotBlank
    String password;

    @Email
    @NotBlank
    String email;

    UserType userType;

}
