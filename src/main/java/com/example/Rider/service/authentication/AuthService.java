package com.example.Rider.service.authentication;

import com.example.Rider.dto.model.UserDTO;
import com.example.Rider.dto.request.auth.LoginRequest;
import com.example.Rider.dto.response.auth.LoginResponse;
import com.example.Rider.dto.response.common.Response;
import com.example.Rider.model.enums.ERole;
import com.example.Rider.model.user.User;
import com.example.Rider.model.user.repository.UserRepository;
import com.example.Rider.security.JwtHelper;
import com.example.Rider.security.UserPrinciple;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Response<LoginResponse> login(LoginRequest request) {
        Response<LoginResponse> response = new Response<>();

        if (request.getEmail() == null) {
            LOGGER.info("Inside login method and Email is empty");
            throw new IllegalArgumentException("Email is empty");
        } else if (request.getPassword() == null) {
            LOGGER.info("Inside login method and password is empty");
            throw new IllegalArgumentException("Password is empty");
        }
        User user = userRepository.findByEmailAndIsDeleted(request.getEmail(), "NO")
                .orElseThrow(() -> new IllegalArgumentException("User info not found for email: " + request.getEmail()));

        if (!user.getIsEnabled()) {
            throw new IllegalArgumentException(
                    "You are not an active user, Please contact with admin for more details.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrinciple userDetails = (UserPrinciple) authentication.getPrincipal();
            String jwt = jwtHelper.generateToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            LOGGER.info("Inside login method and login success!");
            LoginResponse loginResponse = new LoginResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getUsername(),
                    roles
            );

            response.setSuccess(true);
            response.setStatusCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Login success");
            response.setData(loginResponse);

            return response;
        } catch (BadCredentialsException e) {
            LOGGER.info("Inside login method and login failed due to Bad credential");
            response.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
            response.setSuccess(false);
            response.setMessage(e.getLocalizedMessage());
            return response;
        } catch (Exception e) {
            LOGGER.info("Inside login method and login failed due to exception");
            response.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setSuccess(false);
            response.setMessage("Network error during login. Please try again!");
            return response;
        }
    }

    @Override
    public UserDTO validateToken() {
        UserPrinciple userPrincipal = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userPrincipal.getId());
        userDTO.setEmail(userPrincipal.getUsername());
        userDTO.setEnabled(userPrincipal.isEnabled());

        List<String> role = new ArrayList<>();
        for (GrantedAuthority item : userPrincipal.getAuthorities()) {
            role.add(item.getAuthority());
            if (item.getAuthority().equals(ERole.ROLE_SUPER_ADMIN.getDisplayName()) ||
                    item.getAuthority().equals(ERole.ROLE_SUPER_ADMIN.name())) {
                userDTO.setSuperAdmin(true);
            }
            if (item.getAuthority().equals(ERole.ROLE_ADMIN.getDisplayName()) ||
                    item.getAuthority().equals(ERole.ROLE_ADMIN.name())) {
                userDTO.setAdmin(true);
            }
        }

        userDTO.setRole(role);

        return userDTO;
    }
}
