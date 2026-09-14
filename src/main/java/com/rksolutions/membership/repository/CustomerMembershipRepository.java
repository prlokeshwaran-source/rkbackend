package com.rksolutions.membership.repository;

import com.rksolutions.membership.entity.CustomerMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerMembershipRepository extends JpaRepository<CustomerMembership, Long> {
    List<CustomerMembership> findByCustomerId(Long customerId);

    List<CustomerMembership> findByStatus(CustomerMembership.MembershipStatus status);
}
