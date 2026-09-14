package com.rksolutions.customer.dto;

import com.rksolutions.common.enums.CustomerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String email;

    private String city;

    private String interestedIn;

    private String remarks;

    private CustomerStatus status;

    private Long assignedToId;
}
