package com.example.Rider.service.user;

import com.example.Rider.dto.request.ChangePasswordDTO;
import com.example.Rider.dto.request.auth.SignupRequest;
import com.example.Rider.dto.request.common.CustomPageRequest;
import com.example.Rider.model.user.User;
import org.springframework.data.domain.Page;
import com.example.Rider.dto.response.common.Response;

import java.util.List;
import java.util.Map;

public interface IUserService {

    Response<Page<User>> getUsers(CustomPageRequest request);

    public Response<User> getUserDetails(Long id);

    public Response<String> saveUser(SignupRequest request);

    public Response<User> updateUser(SignupRequest request);

    public Response<User> deleteUser(Long id);

    Response<List<Map<String, String>>> getRoleType();

    Response<String> resetUserPassword(Long userId);

    Response<String> changeUserPassword(ChangePasswordDTO request);
}
