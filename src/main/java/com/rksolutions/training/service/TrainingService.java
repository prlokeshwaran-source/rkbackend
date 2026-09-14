package com.rksolutions.training.service;

import com.rksolutions.training.dto.TrainingResourceRequest;
import com.rksolutions.training.dto.TrainingResourceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingService {
    TrainingResourceResponse createResource(TrainingResourceRequest request);
    TrainingResourceResponse updateResource(Long id, TrainingResourceRequest request);
    TrainingResourceResponse getResourceById(Long id);
    List<TrainingResourceResponse> getAllResources();
    List<TrainingResourceResponse> getActiveResources();
    List<TrainingResourceResponse> getResourcesByType(String resourceType);
    Page<TrainingResourceResponse> getAllResourcesPaginated(Pageable pageable);
    void deleteResource(Long id);
}
