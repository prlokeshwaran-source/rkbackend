package com.rksolutions.customer.dto;

import com.rksolutions.common.enums.CustomerStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatusChangeRequest {
    @NotBlank(message = "Status is required")
    private CustomerStatus status;

    private String remarks;

    private Long changedById;
}
