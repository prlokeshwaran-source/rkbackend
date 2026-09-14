package com.rksolutions.training.controller;

import com.rksolutions.common.response.ApiResponse;
import com.rksolutions.training.dto.TrainingResourceRequest;
import com.rksolutions.training.dto.TrainingResourceResponse;
import com.rksolutions.training.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrainingResourceResponse>>> getActiveResources(
            @RequestParam(required = false) String type) {
        List<TrainingResourceResponse> resources = type != null
                ? trainingService.getResourcesByType(type)
                : trainingService.getActiveResources();
        return ResponseEntity.ok(ApiResponse.success(resources, "Training resources retrieved successfully"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TrainingResourceResponse>>> getAllResources() {
        List<TrainingResourceResponse> resources = trainingService.getAllResources();
        return ResponseEntity.ok(ApiResponse.success(resources, "All training resources retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingResourceResponse>> getResourceById(@PathVariable Long id) {
        TrainingResourceResponse response = trainingService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Training resource retrieved successfully"));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TrainingResourceResponse>> createResource(
            @Valid @RequestBody TrainingResourceRequest request) {
        TrainingResourceResponse response = trainingService.createResource(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Training resource created successfully"));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TrainingResourceResponse>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody TrainingResourceRequest request) {
        TrainingResourceResponse response = trainingService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Training resource updated successfully"));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        trainingService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Training resource deleted successfully"));
    }
}
