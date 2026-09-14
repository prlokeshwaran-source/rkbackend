package com.rksolutions.followup.service;

import com.rksolutions.followup.dto.FollowUpRequest;
import com.rksolutions.followup.dto.FollowUpResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FollowUpService {

    FollowUpResponse createFollowUp(FollowUpRequest request);

    FollowUpResponse getFollowUpById(Long id);

    List<FollowUpResponse> getAllFollowUps();

    List<FollowUpResponse> getTodayFollowUps(Long userId);

    List<FollowUpResponse> getUpcomingFollowUps(Long userId);

    FollowUpResponse completeFollowUp(Long id, String remarks);

    void deleteFollowUp(Long id);
}
