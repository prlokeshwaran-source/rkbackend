package com.rksolutions.user.mapper;

import com.rksolutions.common.entity.Role;
import com.rksolutions.common.enums.RoleName;
import com.rksolutions.user.dto.UserRequest;
import com.rksolutions.user.dto.UserResponse;
import com.rksolutions.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setProfileImage(user.getProfileImage());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        if (user.getRoles() != null) {
            Set<RoleName> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            response.setRoles(roleNames);
        }

        return response;
    }

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setProfileImage(request.getProfileImage());

        if (request.getRole() != null) {
            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setName(request.getRole());
            roles.add(role);
            user.setRoles(roles);
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }


        return user;
    }

    public void updateEntityFromRequest(UserRequest request, User user) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }
        
    }
}
