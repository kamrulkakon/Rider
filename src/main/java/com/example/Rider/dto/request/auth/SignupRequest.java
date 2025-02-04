package com.example.Rider.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String role;
}