package com.rksolutions.user.dto;

import com.rksolutions.common.enums.RoleName;
import com.rksolutions.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private Set<RoleName> roles;
    private UserStatus status;
    private Long managerId;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
