package com.rksolutions.customer.repository;

import com.rksolutions.customer.entity.Customer;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    List<Customer> findByStatus(CustomerStatus status);

    List<Customer> findByAssignedTo(User user);

    List<Customer> findByCreatedBy(User user);

    Page<Customer> findByAssignedTo(User user, Pageable pageable);

    Page<Customer> findByCreatedBy(User user, Pageable pageable);

    boolean existsByPhone(String phone);
}
