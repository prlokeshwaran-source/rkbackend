package com.rksolutions.user.service;

import com.rksolutions.user.dto.UserRequest;
import com.rksolutions.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    Page<UserResponse> getAllUsersPaginated(Pageable pageable);

    List<UserResponse> getUsersByRole(String role);

    void updateUserStatus(Long id, String status);

    void deleteUser(Long id);
}
