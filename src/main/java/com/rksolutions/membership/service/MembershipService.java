package com.rksolutions.membership.service;

import com.rksolutions.membership.dto.MembershipPlanRequest;
import com.rksolutions.membership.dto.MembershipPlanResponse;
import com.rksolutions.membership.dto.CustomerMembershipRequest;
import com.rksolutions.membership.dto.CustomerMembershipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MembershipService {

    MembershipPlanResponse createPlan(MembershipPlanRequest request);

    MembershipPlanResponse updatePlan(Long id, MembershipPlanRequest request);

    MembershipPlanResponse getPlanById(Long id);

    List<MembershipPlanResponse> getAllPlans();

    List<MembershipPlanResponse> getActivePlans();

    Page<MembershipPlanResponse> getAllPlansPaginated(Pageable pageable);

    void deletePlan(Long id);

    CustomerMembershipResponse assignMembership(CustomerMembershipRequest request);

    CustomerMembershipResponse getMembershipById(Long id);

    List<CustomerMembershipResponse> getMembershipsByCustomer(Long customerId);
}
