package com.rksolutions.followup.service;

import com.rksolutions.customer.entity.Customer;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.followup.dto.FollowUpRequest;
import com.rksolutions.followup.dto.FollowUpResponse;
import com.rksolutions.followup.entity.FollowUp;
import com.rksolutions.followup.mapper.FollowUpMapper;
import com.rksolutions.followup.repository.FollowUpRepository;
import com.rksolutions.common.enums.FollowUpStatus;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowUpMapper followUpMapper;

    @Override
    public FollowUpResponse createFollowUp(FollowUpRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        FollowUp followUp = new FollowUp();
        followUp.setCustomer(customer);
        followUp.setScheduledAt(request.getScheduledAt());
        followUp.setStatus(request.getStatus() != null ? request.getStatus() : FollowUpStatus.PENDING);
        followUp.setRemarks(request.getRemarks());
        followUp.setCallType(request.getCallType());

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            followUp.setAssignedTo(assignedTo);
        }

        FollowUp saved = followUpRepository.save(followUp);
        return followUpMapper.toResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public FollowUpResponse getFollowUpById(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FollowUp not found with id: " + id));
        return followUpMapper.toResponse(followUp);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<FollowUpResponse> getAllFollowUps() {
        return followUpRepository.findAll().stream()
                .map(followUpMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<FollowUpResponse> getTodayFollowUps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<FollowUp> followUps = followUpRepository.findByAssignedToAndStatus(
                user, FollowUpStatus.PENDING);

        return followUps.stream()
                .filter(f -> !f.getScheduledAt().isBefore(startOfDay) && !f.getScheduledAt().isAfter(endOfDay))
                .map(followUpMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<FollowUpResponse> getUpcomingFollowUps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();

        List<FollowUp> followUps = followUpRepository.findByAssignedToAndStatus(
                user, FollowUpStatus.PENDING);

        return followUps.stream()
                .filter(f -> f.getScheduledAt().isAfter(now))
                .map(followUpMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FollowUpResponse completeFollowUp(Long id, String remarks) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FollowUp not found with id: " + id));

        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp.setCompletedAt(LocalDateTime.now());
        followUp.setRemarks(followUp.getRemarks() != null
                ? followUp.getRemarks() + "\n" + remarks : remarks);

        FollowUp saved = followUpRepository.save(followUp);
        return followUpMapper.toResponse(saved);
    }

    @Override
    public void deleteFollowUp(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FollowUp not found with id: " + id));
        followUpRepository.delete(followUp);
    }
}
