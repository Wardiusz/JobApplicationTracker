package com.wardiusz.jat.model.request;

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
    @Size(min = 3, max = 20, message = "Nazwa użytkownika musi zawierać od 3 do 20 znaków")
    private String username;

    @Email
    @NotBlank(message = "Adres email nie może być pusty")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Hasło musi zawierać minimum 8 znaków")
    private String password;

    UserType userType;

}
