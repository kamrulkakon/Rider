package com.example.Rider.service.authentication;

import com.example.Rider.dto.model.UserDTO;
import com.example.Rider.dto.request.auth.LoginRequest;
import com.example.Rider.dto.response.auth.LoginResponse;
import com.example.Rider.dto.response.common.Response;

public interface IAuthService {
    Response<LoginResponse> login(LoginRequest loginRequest);
    UserDTO validateToken();
}
