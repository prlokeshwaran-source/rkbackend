package com.rksolutions.training.dto;

import com.rksolutions.common.enums.ResourceType;
import com.rksolutions.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingResourceResponse {

    private Long id;
    private String title;
    private String description;
    private ResourceType resourceType;
    private String fileUrl;
    private String thumbnailUrl;
    private Boolean active;
    private String category;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
