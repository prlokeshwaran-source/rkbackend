package com.rksolutions.training.service;

import com.rksolutions.common.enums.ResourceType;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.training.dto.TrainingResourceRequest;
import com.rksolutions.training.dto.TrainingResourceResponse;
import com.rksolutions.training.entity.TrainingResource;
import com.rksolutions.training.mapper.TrainingMapper;
import com.rksolutions.training.repository.TrainingResourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingResourceRepository repository;

    @Autowired
    private TrainingMapper mapper;

    @Override
    public TrainingResourceResponse createResource(TrainingResourceRequest request) {
        TrainingResource resource = mapper.toEntity(request);
        TrainingResource saved = repository.save(resource);
        return mapper.toResponse(saved);
    }

    @Override
    public TrainingResourceResponse updateResource(Long id, TrainingResourceRequest request) {
        TrainingResource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training resource not found with id: " + id));
        mapper.updateEntityFromRequest(request, resource);
        TrainingResource saved = repository.save(resource);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public TrainingResourceResponse getResourceById(Long id) {
        TrainingResource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training resource not found with id: " + id));
        return mapper.toResponse(resource);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<TrainingResourceResponse> getAllResources() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<TrainingResourceResponse> getActiveResources() {
        return repository.findByActiveTrue().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<TrainingResourceResponse> getResourcesByType(String resourceType) {
        try {
            ResourceType type = ResourceType.valueOf(resourceType.toUpperCase());
            return repository.findByResourceType(type).stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource type: " + resourceType);
        }
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<TrainingResourceResponse> getAllResourcesPaginated(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Override
    public void deleteResource(Long id) {
        TrainingResource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training resource not found with id: " + id));
        repository.delete(resource);
    }
}
