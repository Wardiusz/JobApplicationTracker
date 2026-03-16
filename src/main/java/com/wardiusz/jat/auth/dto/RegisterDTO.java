package com.wardiusz.jat.auth.dto;

import com.wardiusz.jat.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank
    String username;

    @Email
    @NotBlank
    String email;

    @Size(min = 8)
    @NotBlank
    String password;

    UserType userType;
}
