package com.example.Rider.dto.response.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public LoginResponse(
            String accessToken,
            Long id,
            String username,
            String email,
            List<String> roles
    ) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
