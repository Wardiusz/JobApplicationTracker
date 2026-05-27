package com.wardiusz.jat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

}
