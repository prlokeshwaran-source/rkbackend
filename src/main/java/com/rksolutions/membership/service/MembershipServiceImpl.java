package com.rksolutions.membership.service;

import com.rksolutions.customer.entity.Customer;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.membership.dto.CustomerMembershipRequest;
import com.rksolutions.membership.dto.CustomerMembershipResponse;
import com.rksolutions.membership.dto.MembershipPlanRequest;
import com.rksolutions.membership.dto.MembershipPlanResponse;
import com.rksolutions.membership.entity.CustomerMembership;
import com.rksolutions.membership.entity.MembershipPlan;
import com.rksolutions.membership.mapper.MembershipMapper;
import com.rksolutions.membership.repository.CustomerMembershipRepository;
import com.rksolutions.membership.repository.MembershipPlanRepository;
import com.rksolutions.common.exception.ResourceNotFoundException;
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
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private CustomerMembershipRepository membershipRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MembershipMapper mapper;

    @Override
    public MembershipPlanResponse createPlan(MembershipPlanRequest request) {
        MembershipPlan plan = mapper.toPlanEntity(request);
        MembershipPlan saved = planRepository.save(plan);
        return mapper.toPlanResponse(saved);
    }

    @Override
    public MembershipPlanResponse updatePlan(Long id, MembershipPlanRequest request) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        mapper.updatePlanEntityFromRequest(request, plan);
        MembershipPlan saved = planRepository.save(plan);
        return mapper.toPlanResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public MembershipPlanResponse getPlanById(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        return mapper.toPlanResponse(plan);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<MembershipPlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(mapper::toPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<MembershipPlanResponse> getActivePlans() {
        return planRepository.findByActiveTrue().stream()
                .map(mapper::toPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<MembershipPlanResponse> getAllPlansPaginated(Pageable pageable) {
        return planRepository.findAll(pageable)
                .map(mapper::toPlanResponse);
    }

    @Override
    public void deletePlan(Long id) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + id));
        planRepository.delete(plan);
    }

    @Override
    public CustomerMembershipResponse assignMembership(CustomerMembershipRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        MembershipPlan plan = planRepository.findById(request.getMembershipPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id: " + request.getMembershipPlanId()));

        CustomerMembership membership = new CustomerMembership();
        membership.setCustomer(customer);
        membership.setMembershipPlan(plan);
        membership.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now());

        if (request.getDurationDays() != null) {
            membership.setExpiryDate(membership.getStartDate().plusDays(request.getDurationDays()));
        } else {
            membership.setExpiryDate(membership.getStartDate().plusDays(plan.getDuration()));
        }

        membership.setTotalAmount(plan.getPrice().subtract(request.getDiscountAmount()));
        membership.setDiscountAmount(request.getDiscountAmount());
        membership.setStatus(CustomerMembership.MembershipStatus.ACTIVE);

        CustomerMembership saved = membershipRepository.save(membership);
        return mapper.toMembershipResponse(saved);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CustomerMembershipResponse getMembershipById(Long id) {
        CustomerMembership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id: " + id));
        return mapper.toMembershipResponse(membership);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CustomerMembershipResponse> getMembershipsByCustomer(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        return membershipRepository.findByCustomerId(customerId).stream()
                .map(mapper::toMembershipResponse)
                .collect(Collectors.toList());
    }
}
