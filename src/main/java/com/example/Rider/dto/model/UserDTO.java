package com.example.Rider.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private boolean isEnabled;
    List<String> role;
    private boolean isAdmin;
    private boolean isSuperAdmin;
}
