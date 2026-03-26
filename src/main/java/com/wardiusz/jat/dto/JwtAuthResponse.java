package com.wardiusz.jat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
