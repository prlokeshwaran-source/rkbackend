package com.rksolutions.customer.repository;

import com.rksolutions.customer.entity.CustomerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerStatusHistoryRepository extends JpaRepository<CustomerStatusHistory, Long> {
    List<CustomerStatusHistory> findByCustomerId(Long customerId);
}
