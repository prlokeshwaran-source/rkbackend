package com.rksolutions.training.dto;

import com.rksolutions.common.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingResourceRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private ResourceType resourceType;

    private String fileUrl;

    private String thumbnailUrl;

    private Boolean active = true;

    private String category;
}
