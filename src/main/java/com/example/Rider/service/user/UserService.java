package com.example.Rider.service.user;

import com.example.Rider.dto.request.ChangePasswordDTO;
import com.example.Rider.dto.request.auth.SignupRequest;
import com.example.Rider.dto.request.common.CustomPageRequest;
import com.example.Rider.dto.response.common.Response;
import com.example.Rider.exception.RecordNotFoundException;
import com.example.Rider.model.enums.ERole;
import com.example.Rider.model.user.Role;
import com.example.Rider.model.user.User;
import com.example.Rider.model.user.repository.RoleRepository;
import com.example.Rider.model.user.repository.UserRepository;
import com.example.Rider.security.UserPrinciple;
import com.example.Rider.service.log.IErrorLogService;
import com.example.Rider.utils.CustomMessage;
import com.example.Rider.utils.StatusCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IErrorLogService errorLogService;

    @Override
    public Response<Page<User>> getUsers(CustomPageRequest request) {
        request.setSize(Math.max(10, request.getSize()));
        request.setPageNo(Math.max(0, request.getPageNo()));
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getSize());

        String firstName = null;
        String middleName = null;
        String lastName = null;

        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String[] nameList = request.getKeyword().split(" ");
            firstName = nameList[0].toLowerCase();

            if (nameList.length == 2) {
                lastName = nameList[1].toLowerCase();
            }
            if (nameList.length == 3) {
                middleName = nameList[1].toLowerCase();
                lastName = nameList[2].toLowerCase();
            }
            request.setKeyword(request.getKeyword().toLowerCase());
        }

        Page<User> userList = userRepository.getUserListWithPagination(
                request.getKeyword(),
                firstName,
                middleName,
                lastName,
                "NO",
                pageable
        );

        return new Response<>(StatusCode.OK, true, CustomMessage.DATA_FOUND, userList);
    }

    @Override
    public Response<User> getUserDetails(Long id) {
        try {
            User user = userRepository.findByIdAndIsDeleted(id, "NO")
                    .orElse(null);
            if (user != null) {
                return new Response<>(StatusCode.OK, true, CustomMessage.DATA_FOUND, user);
            } else {
                return new Response<>(StatusCode.OK, true, CustomMessage.NO_RECORD_FOUND + id, null);
            }
        } catch (Exception ex) {
            errorLogService.saveErrorLog("Get User", ex.toString());
            return new Response<>(StatusCode.BAD_REQUEST, false, CustomMessage.FAILED_TO_GET_DATA, null);
        }
    }

    @Override
    public Response<String> saveUser(SignupRequest request) {
        Response<String> response = new Response<>();
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            return new Response<>(StatusCode.NO_CONTENT, false, "First name is null", null);
        } else if (request.getLastName() == null || request.getLastName().isEmpty()) {
            return new Response<>(StatusCode.NO_CONTENT, false, "Last name is null", null);
        } else if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return new Response<>(StatusCode.NO_CONTENT, false, "Email is null", null);
        } else if (request.getRole() == null || request.getRole().isEmpty()) {
            return new Response<>(StatusCode.NO_CONTENT, false,
                    "Role is null", null
            );
        } else if (
                request.getRole().equals(ERole.ROLE_SUPER_ADMIN.getDisplayName()) ||
                        request.getRole().equals(ERole.ROLE_SUPER_ADMIN.name())
        ) {
            boolean isAdmin = userPrinciple.getAuthorities().stream()
                    .anyMatch(
                            authority -> authority
                                    .getAuthority()
                                    .equals(ERole.ROLE_ADMIN.getDisplayName()) ||
                                    authority
                                            .getAuthority()
                                            .equals(ERole.ROLE_ADMIN.name())
                    );
            if (isAdmin) {
                return new Response<>(
                        String.valueOf(HttpStatus.FORBIDDEN.value()),
                        false,
                        "You do not have permission to create super user!",
                        "You do not have permission to create super user!"
                );
            }
        } else if (userRepository.existsByEmailAndIsDeleted(request.getEmail(), "NO")) {
            return new Response<>(
                    StatusCode.NO_CONTENT,
                    false,
                    "User with email: " + request.getEmail() + CustomMessage.ALREADY_EXIST,
                    null
            );
        } else if (userRepository.existsByEmail(request.getEmail())) {
            return new Response<>(
                    StatusCode.NO_CONTENT,
                    false,
                    "Id with email " + request.getEmail() + " is deleted. Please contact with admin.",
                    null
            );
        }

        User user = new User();
        user.setFirstName(request.getFirstName());

        if (request.getMiddleName() != null) {
            user.setMiddleName(request.getMiddleName());
        }

        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode("12345678"));

        user.setCreatedBy(userPrinciple.getId());

        user.setRoles(roleRepository.findByRoleName(ERole.valueOf(request.getRole())).stream().toList());

        try {
            userRepository.save(user);
            response.setSuccess(true);
            response.setStatusCode(StatusCode.CREATED);
            response.setMessage("User" + CustomMessage.SAVE_SUCCESS_MESSAGE);
        } catch (Exception ex) {
            LOGGER.info("Inside user service save method and save failed due to Exception");
            errorLogService.saveErrorLog("Save User", ex.toString());

            response.setSuccess(false);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setMessage(request.getEmail() + CustomMessage.SAVE_FAILED_MESSAGE);
        }

        return response;
    }

    @Override
    public Response<User> updateUser(SignupRequest request) {
        Response<User> response = new Response<>();
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (request.getFirstName() == null) {
            return new Response<>(StatusCode.NO_CONTENT, false, "First name is null", null);
        } else if (request.getLastName() == null) {
            return new Response<>(StatusCode.NO_CONTENT, false, "Last name is null", null);
        } else if (request.getEmail() == null) {
            return new Response<>(StatusCode.NO_CONTENT, false, "Email is null", null);
        } else if (request.getRole() == null || request.getRole().isEmpty()) {
            return new Response<>(
                    StatusCode.NO_CONTENT, false,
                    "Role is null", null
            );
        } else if (request.getRole().equals(ERole.ROLE_SUPER_ADMIN.getDisplayName()) ||
                request.getRole().equals(ERole.ROLE_SUPER_ADMIN.name())) {
            boolean isAdmin = userPrinciple.getAuthorities().stream()
                    .anyMatch(
                            authority -> authority
                                    .getAuthority()
                                    .equals(ERole.ROLE_ADMIN.getDisplayName()) ||
                                    authority
                                            .getAuthority()
                                            .equals(ERole.ROLE_ADMIN.name())
                    );
            if (isAdmin) {
                return new Response<>(
                        String.valueOf(HttpStatus.FORBIDDEN.value()),
                        false,
                        "You do not have permission to update super user!",
                        null
                );
            }
        } else if (!userRepository.existsByIdAndIsDeleted(request.getId(), "NO")) {
            return new Response<>(StatusCode.BAD_REQUEST, false,
                    CustomMessage.NO_RECORD_FOUND + request.getId(), null);
        }

        User user = userRepository.findByIdAndIsDeleted(request.getId(), "NO")
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getMiddleName() != null) {
            user.setMiddleName(request.getMiddleName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            Role userRole = roleRepository.findByRoleName(ERole.valueOf(request.getRole()))
                    .orElseThrow(() -> new UsernameNotFoundException("Role not found!"));
            Collection<Role> roles = new ArrayList<>();
            roles.add(userRole);
            user.setRoles(roles);
        }

        user.setUpdatedAt(new Date());
        user.setUpdatedBy(userPrinciple.getId());

        try {
            userRepository.save(user);
            response.setSuccess(true);
            response.setStatusCode(StatusCode.CREATED);
            response.setData(user);
            response.setMessage("User" + CustomMessage.UPDATE_SUCCESS_MESSAGE);
        } catch (Exception ex) {
            LOGGER.info("Inside user service update method and update failed due to Exception");

            errorLogService.saveErrorLog("Update User", ex.toString());
            response.setSuccess(false);
            response.setStatusCode(StatusCode.BAD_REQUEST);
            response.setMessage(CustomMessage.UPDATE_FAILED_MESSAGE);
        }
        return response;
    }

    @Override
    public Response<User> deleteUser(Long id) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userRepository.findByIdAndIsDeleted(id, "NO")
                .orElse(null);

        if (user == null) {
            return new Response<>(StatusCode.BAD_REQUEST, false, CustomMessage.NO_RECORD_FOUND + id,
                    null);
        }

        user.setIsDeleted("YES");
        user.setUpdatedBy(userPrinciple.getId());
        user.setUpdatedAt(new Date());

        try {
            userRepository.save(user);
        } catch (Exception ex) {
            LOGGER.info("Inside user service delete method and delete failed due to Exception");

            errorLogService.saveErrorLog("Delete User", ex.toString());
            return new Response<>(StatusCode.BAD_REQUEST, false, CustomMessage.DELETE_FAILED_MESSAGE,
                    null);
        }

        return new Response<>(
                StatusCode.CREATED,
                true,
                user.getDisplayName() + CustomMessage.DELETE_SUCCESS_MESSAGE,
                user
        );
    }

    @Override
    public Response<List<Map<String, String>>> getRoleType() {
        Response<List<Map<String, String>>> response = new Response<>();
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = null;

        for (ERole item : ERole.getSortedValue()) {
            map = new HashMap<>();
            map.put("roleName", item.name());
            map.put("value", item.getDisplayName());
            mapList.add(map);
        }

        response.setSuccess(true);
        response.setStatusCode(StatusCode.OK);
        response.setMessage(CustomMessage.DATA_FOUND);
        response.setData(mapList);

        return response;
    }

    @Override
    public Response<String> resetUserPassword(Long userId) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (userId == null) {
            return new Response<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    false,
                    "User id is null!",
                    "User id is null!"
            );
        }

        boolean isSuperAdmin = userPrinciple.getAuthorities().stream()
                .anyMatch(
                        authority -> authority
                                .getAuthority()
                                .equals(ERole.ROLE_SUPER_ADMIN.getDisplayName()) ||
                                authority
                                        .getAuthority()
                                        .equals(ERole.ROLE_SUPER_ADMIN.name())
                );

        if (!isSuperAdmin) {
            return new Response<>(
                    String.valueOf(HttpStatus.FORBIDDEN.value()),
                    false,
                    "You do not have permission to reset user password!",
                    "You do not have permission to reset user password!"
            );
        }

        try {
            User user = userRepository.findByIdAndIsDeleted(userId, "NO")
                    .orElseThrow(() -> new RecordNotFoundException("User " + userId + " does not exist!"));
            user.setPassword(encoder.encode("12345678"));
            user.setUpdatedAt(new Date());
            user.setUpdatedBy(userPrinciple.getId());
            userRepository.save(user);

            return new Response<>(
                    String.valueOf(HttpStatus.OK.value()),
                    true,
                    "Password reset successfully!",
                    "Password reset successfully!"
            );
        } catch (RuntimeException ex) {
            LOGGER.info("Inside user password reset service method and password reset failed due to Exception: {}",
                    ex.getMessage());
            errorLogService.saveErrorLog("User password reset", ex.toString());
            return new Response<>(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    false,
                    "Password reset failed!",
                    "Password reset failed!"
            );
        }
    }

    @Override
    public Response<String> changeUserPassword(ChangePasswordDTO request) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        if (
                request.getOldPassword() == null || request.getOldPassword().isEmpty() ||
                        request.getNewPassword() == null || request.getNewPassword().isEmpty() ||
                        request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()
        ) {
            return new Response<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    false,
                    "Required data is missing or invalid",
                    "Required data is missing or invalid"
            );
        }

        if (request.getNewPassword().equals(request.getOldPassword())) {
            return new Response<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    false,
                    "New password cannot be same as old password!",
                    "New password cannot be same as old password!"
            );
        }

        if (request.getNewPassword().contains(" ")) {
            return new Response<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    false,
                    "New password cannot contain spaces!",
                    "New password cannot contain spaces!"
            );
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return new Response<>(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    false,
                    "New password and confirm password does not match!",
                    "New password and confirm password does not match!"
            );
        }

        try {
            User user = userRepository.findByIdAndIsDeleted(userPrinciple.getId(), "NO")
                    .orElseThrow(() -> new RecordNotFoundException("User " + userPrinciple.getId() + " does not exist!"));
            if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
                return new Response<>(
                        String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        false,
                        "Old password is incorrect!",
                        "Old password is incorrect!"
                );
            }
            user.setPassword(encoder.encode(request.getNewPassword()));
            user.setUpdatedAt(new Date());
            user.setUpdatedBy(userPrinciple.getId());
            userRepository.save(user);
            return new Response<>(
                    String.valueOf(HttpStatus.OK.value()),
                    true,
                    "Password successfully changed!",
                    "Password successfully changed!"
            );
        } catch (RuntimeException ex) {
            LOGGER.info("Inside user password changed service method and password changed failed due to Exception: {}",
                    ex.getMessage());
            errorLogService.saveErrorLog("User password reset", ex.toString());
            return new Response<>(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    false,
                    "Password changed failed!",
                    "Password changed failed!"
            );
        }
    }
}
