package com.rksolutions.user.dto;

import com.rksolutions.common.enums.RoleName;
import com.rksolutions.common.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be 10-15 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, max = 120, message = "Password must be at least 6 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleName role;

    private Long managerId;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
