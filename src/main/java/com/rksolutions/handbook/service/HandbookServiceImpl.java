package com.rksolutions.handbook.service;

import com.rksolutions.handbook.dto.HandbookRequest;
import com.rksolutions.handbook.dto.HandbookResponse;
import com.rksolutions.handbook.entity.Handbook;
import com.rksolutions.handbook.mapper.HandbookMapper;
import com.rksolutions.handbook.repository.HandbookRepository;
import com.rksolutions.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HandbookServiceImpl implements HandbookService {

    @Autowired
    private HandbookRepository handbookRepository;

    @Autowired
    private HandbookMapper handbookMapper;

    @Override
    public HandbookResponse createHandbook(HandbookRequest request) {
        Handbook handbook = handbookMapper.toEntity(request);
        Handbook saved = handbookRepository.save(handbook);
        return handbookMapper.toResponse(saved);
    }

    @Override
    public HandbookResponse updateHandbook(Long id, HandbookRequest request) {
        Handbook handbook = handbookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Handbook not found with id: " + id));
        handbookMapper.updateEntityFromRequest(request, handbook);
        Handbook saved = handbookRepository.save(handbook);
        return handbookMapper.toResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public HandbookResponse getHandbookById(Long id) {
        Handbook handbook = handbookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Handbook not found with id: " + id));
        return handbookMapper.toResponse(handbook);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<HandbookResponse> getAllHandbooks() {
        return handbookRepository.findAll().stream()
                .map(handbookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<HandbookResponse> getActiveHandbooks() {
        return handbookRepository.findByActiveTrue().stream()
                .map(handbookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<HandbookResponse> getAllHandbooksPaginated(Pageable pageable) {
        return handbookRepository.findAll(pageable)
                .map(handbookMapper::toResponse);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<HandbookResponse> getByCategory(String category) {
        return handbookRepository.findByCategory(category).stream()
                .map(handbookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteHandbook(Long id) {
        Handbook handbook = handbookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Handbook not found with id: " + id));
        handbookRepository.delete(handbook);
    }
}
