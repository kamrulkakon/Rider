package com.example.Rider.controller.v1;

import com.example.Rider.dto.request.auth.LoginRequest;
import com.example.Rider.dto.response.auth.LoginResponse;
import com.example.Rider.dto.response.common.Response;
import com.example.Rider.service.authentication.IAuthService;
import com.example.Rider.utils.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Api.BASE_API)
public class AuthController {

    private final IAuthService authService;

    @PostMapping(Api.LOGIN)
    public Response<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
