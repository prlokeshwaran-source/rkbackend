package com.rksolutions.handbook.service;

import com.rksolutions.handbook.dto.HandbookRequest;
import com.rksolutions.handbook.dto.HandbookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HandbookService {
    HandbookResponse createHandbook(HandbookRequest request);
    HandbookResponse updateHandbook(Long id, HandbookRequest request);
    HandbookResponse getHandbookById(Long id);
    List<HandbookResponse> getAllHandbooks();
    List<HandbookResponse> getActiveHandbooks();
    Page<HandbookResponse> getAllHandbooksPaginated(Pageable pageable);
    List<HandbookResponse> getByCategory(String category);
    void deleteHandbook(Long id);
}
