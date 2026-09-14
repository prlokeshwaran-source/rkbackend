package com.rksolutions.handbook.controller;

import com.rksolutions.handbook.dto.HandbookRequest;
import com.rksolutions.handbook.dto.HandbookResponse;
import com.rksolutions.handbook.service.HandbookService;
import com.rksolutions.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HandbookController {

    @Autowired
    private HandbookService handbookService;

    @GetMapping("/handbooks")
    public ResponseEntity<ApiResponse<List<HandbookResponse>>> getActiveHandbooks(
            @RequestParam(required = false) String category) {
        List<HandbookResponse> handbooks = category != null
                ? handbookService.getByCategory(category)
                : handbookService.getActiveHandbooks();
        return ResponseEntity.ok(ApiResponse.success(handbooks, "Handbooks retrieved successfully"));
    }

    @GetMapping("/handbooks/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<HandbookResponse>>> getAllHandbooks() {
        List<HandbookResponse> handbooks = handbookService.getAllHandbooks();
        return ResponseEntity.ok(ApiResponse.success(handbooks, "All handbooks retrieved successfully"));
    }

    @GetMapping("/handbooks/{id}")
    public ResponseEntity<ApiResponse<HandbookResponse>> getHandbookById(@PathVariable Long id) {
        HandbookResponse response = handbookService.getHandbookById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Handbook retrieved successfully"));
    }

    @PostMapping("/admin/handbooks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HandbookResponse>> createHandbook(
            @Valid @RequestBody HandbookRequest request) {
        HandbookResponse response = handbookService.createHandbook(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Handbook created successfully"));
    }

    @PutMapping("/admin/handbooks/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<HandbookResponse>> updateHandbook(
            @PathVariable Long id,
            @Valid @RequestBody HandbookRequest request) {
        HandbookResponse response = handbookService.updateHandbook(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Handbook updated successfully"));
    }

    @DeleteMapping("/admin/handbooks/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHandbook(@PathVariable Long id) {
        handbookService.deleteHandbook(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Handbook deleted successfully"));
    }
}
