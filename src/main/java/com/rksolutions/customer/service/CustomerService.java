package com.rksolutions.customer.service;

import com.rksolutions.customer.dto.CustomerRequest;
import com.rksolutions.customer.dto.CustomerResponse;
import com.rksolutions.customer.dto.CustomerStatusChangeRequest;
import com.rksolutions.customer.entity.CustomerStatusHistory;
import com.rksolutions.common.enums.CustomerStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> getAllCustomers(CustomerStatus status, Long assignedToId,
                                           String city, String interestedIn, Pageable pageable);

    CustomerResponse changeCustomerStatus(Long id, CustomerStatusChangeRequest request);

    CustomerResponse assignCustomer(Long id, Long assignedToId);

    void deleteCustomer(Long id);

    List<CustomerStatusHistory> getStatusHistory(Long customerId);
}
