package com.rksolutions.handbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandbookResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal commissionAmount;
    private Integer stock;
    private String imageUrl;
    private Boolean active;
    private String category;
    private Boolean isDigital;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
