package com.rksolutions.auth.dto;

import com.rksolutions.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private UserStatus status;
    private String message;
}
