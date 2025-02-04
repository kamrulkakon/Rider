package com.example.Rider.controller.v1;

import com.example.Rider.dto.request.ChangePasswordDTO;
import com.example.Rider.dto.request.auth.SignupRequest;
import com.example.Rider.dto.request.common.CustomPageRequest;
import com.example.Rider.dto.response.common.Response;
import com.example.Rider.model.user.User;
import com.example.Rider.service.user.IUserService;
import com.example.Rider.utils.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(Api.BASE_API + Api.USER)
public class UserController {

    private final IUserService userService;

    @PostMapping()
    public Response<Page<User>> getUserList(@RequestBody CustomPageRequest request) {
        return userService.getUsers(request);
    }

    @GetMapping(Api.DETAILS_BY_ID)
    public Response<User> getUserDetails(@PathVariable("id") Long id) {
        return userService.getUserDetails(id);
    }

    @PreAuthorize(
            "hasAnyAuthority(T(com.nippon.model.enums.ERole).ROLE_SUPER_ADMIN.name(), T(com.nippon.model.enums.ERole).ROLE_ADMIN.name())"
    )
    @PostMapping(Api.SAVE)
    @Operation(summary = "Create new user", description = "This is user API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success | OK"),
            @ApiResponse(responseCode = "401", description = "Not authorized"),
            @ApiResponse(responseCode = "201", description = "New user created")
    })
    public Response<String> saveUser(@RequestBody SignupRequest request) {
        return userService.saveUser(request);
    }

    @PutMapping(Api.UPDATE)
    @PreAuthorize(
            "hasAnyAuthority(T(com.nippon.model.enums.ERole).ROLE_SUPER_ADMIN.name(), T(com.nippon.model.enums.ERole).ROLE_ADMIN.name())"
    )
    public Response<User> updateUser(@RequestBody SignupRequest request) {
        return userService.updateUser(request);
    }

    @DeleteMapping(Api.DELETE_BY_ID)
    @PreAuthorize(
            "hasAnyAuthority(T(com.nippon.model.enums.ERole).ROLE_SUPER_ADMIN.name())"
    )
    public Response<User> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/roleType")
    public Response<List<Map<String, String>>> getRoleType() {
        return userService.getRoleType();
    }

    @PostMapping(Api.RESET_PASSWORD)
    @Operation(summary = "Reset User Password", description = "This is users password reset API")
    @PreAuthorize(
            "hasAnyAuthority(T(com.nippon.model.enums.ERole).ROLE_SUPER_ADMIN.name())"
    )
    public Response<String> resetUserPassword(@PathVariable("user-id") Long userId) {
        return userService.resetUserPassword(userId);
    }

    @PostMapping(Api.CHANGE_PASSWORD)
    @Operation(summary = "Change User Password", description = "This is change user password API")
    public Response<String> changeUserPassword(@RequestBody ChangePasswordDTO request) {
        return userService.changeUserPassword(request);
    }
}
