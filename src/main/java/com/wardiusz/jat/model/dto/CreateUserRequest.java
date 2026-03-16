package com.wardiusz.jat.model.dto;

import com.wardiusz.jat.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateUserRequest {

    @NotBlank
    String username;

    @Email
    @NotBlank
    String email;

    @Size(min = 8)
    @NotBlank
    String password;

    UserType userType = UserType.USER;
}
