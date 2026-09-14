package com.rksolutions.handbook.mapper;

import com.rksolutions.handbook.dto.HandbookRequest;
import com.rksolutions.handbook.dto.HandbookResponse;
import com.rksolutions.handbook.entity.Handbook;
import org.springframework.stereotype.Component;

@Component
public class HandbookMapper {

    public HandbookResponse toResponse(Handbook handbook) {
        if (handbook == null) return null;

        HandbookResponse response = new HandbookResponse();
        response.setId(handbook.getId());
        response.setTitle(handbook.getTitle());
        response.setDescription(handbook.getDescription());
        response.setPrice(handbook.getPrice());
        response.setCommissionAmount(handbook.getCommissionAmount());
        response.setStock(handbook.getStock());
        response.setImageUrl(handbook.getImageUrl());
        response.setActive(handbook.getActive());
        response.setCategory(handbook.getCategory());
        response.setIsDigital(handbook.getIsDigital());
        response.setCreatedAt(handbook.getCreatedAt());
        response.setUpdatedAt(handbook.getUpdatedAt());
        return response;
    }

    public Handbook toEntity(HandbookRequest request) {
        if (request == null) return null;

        Handbook handbook = new Handbook();
        handbook.setTitle(request.getTitle());
        handbook.setDescription(request.getDescription());
        handbook.setPrice(request.getPrice());
        handbook.setCommissionAmount(request.getCommissionAmount());
        handbook.setStock(request.getStock());
        handbook.setImageUrl(request.getImageUrl());
        handbook.setActive(request.getActive());
        handbook.setCategory(request.getCategory());
        handbook.setIsDigital(request.getIsDigital());
        return handbook;
    }

    public void updateEntityFromRequest(HandbookRequest request, Handbook handbook) {
        if (request.getTitle() != null) handbook.setTitle(request.getTitle());
        if (request.getDescription() != null) handbook.setDescription(request.getDescription());
        if (request.getPrice() != null) handbook.setPrice(request.getPrice());
        if (request.getCommissionAmount() != null) handbook.setCommissionAmount(request.getCommissionAmount());
        if (request.getStock() != null) handbook.setStock(request.getStock());
        if (request.getImageUrl() != null) handbook.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) handbook.setActive(request.getActive());
        if (request.getCategory() != null) handbook.setCategory(request.getCategory());
        if (request.getIsDigital() != null) handbook.setIsDigital(request.getIsDigital());
    }
}
