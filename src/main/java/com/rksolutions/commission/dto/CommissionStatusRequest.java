package com.rksolutions.commission.dto;

import com.rksolutions.common.enums.CommissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionStatusRequest {
    private CommissionStatus status;
    private String remarks;
}
