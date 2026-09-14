package com.rksolutions.training.mapper;

import com.rksolutions.training.dto.TrainingResourceRequest;
import com.rksolutions.training.dto.TrainingResourceResponse;
import com.rksolutions.training.entity.TrainingResource;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TrainingResourceResponse toResponse(TrainingResource resource) {
        if (resource == null) return null;

        TrainingResourceResponse response = new TrainingResourceResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setDescription(resource.getDescription());
        response.setResourceType(resource.getResourceType());
        response.setFileUrl(resource.getFileUrl());
        response.setThumbnailUrl(resource.getThumbnailUrl());
        response.setActive(resource.getActive());
        response.setCategory(resource.getCategory());

        if (resource.getCreatedBy() != null) {
            response.setCreatedById(resource.getCreatedBy().getId());
            response.setCreatedByName(resource.getCreatedBy().getName());
        }

        response.setCreatedAt(resource.getCreatedAt());
        response.setUpdatedAt(resource.getUpdatedAt());
        return response;
    }

    public TrainingResource toEntity(TrainingResourceRequest request) {
        if (request == null) return null;

        TrainingResource resource = new TrainingResource();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setResourceType(request.getResourceType());
        resource.setFileUrl(request.getFileUrl());
        resource.setThumbnailUrl(request.getThumbnailUrl());
        resource.setActive(request.getActive());
        resource.setCategory(request.getCategory());
        return resource;
    }

    public void updateEntityFromRequest(TrainingResourceRequest request, TrainingResource resource) {
        if (request.getTitle() != null) resource.setTitle(request.getTitle());
        if (request.getDescription() != null) resource.setDescription(request.getDescription());
        if (request.getResourceType() != null) resource.setResourceType(request.getResourceType());
        if (request.getFileUrl() != null) resource.setFileUrl(request.getFileUrl());
        if (request.getThumbnailUrl() != null) resource.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getActive() != null) resource.setActive(request.getActive());
        if (request.getCategory() != null) resource.setCategory(request.getCategory());
    }
}
