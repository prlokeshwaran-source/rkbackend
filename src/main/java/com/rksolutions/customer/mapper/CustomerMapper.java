package com.rksolutions.customer.mapper;

import com.rksolutions.customer.dto.CustomerRequest;
import com.rksolutions.customer.dto.CustomerResponse;
import com.rksolutions.customer.entity.Customer;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setPhone(customer.getPhone());
        response.setEmail(customer.getEmail());
        response.setCity(customer.getCity());
        response.setInterestedIn(customer.getInterestedIn());
        response.setRemarks(customer.getRemarks());
        response.setStatus(customer.getStatus());
        response.setJoinedAt(customer.getJoinedAt());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        if (customer.getCreatedBy() != null) {
            response.setCreatedBy(customer.getCreatedBy().getId());
            response.setCreatedByName(customer.getCreatedBy().getName());
        }

        if (customer.getAssignedTo() != null) {
            response.setAssignedTo(customer.getAssignedTo().getId());
            response.setAssignedToName(customer.getAssignedTo().getName());
        }

        return response;
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setCity(request.getCity());
        customer.setInterestedIn(request.getInterestedIn());
        customer.setRemarks(request.getRemarks());

        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        } else {
            customer.setStatus(CustomerStatus.NEW);
        }

        if (request.getAssignedToId() != null) {
            customer.setAssignedTo(new User());
            customer.getAssignedTo().setId(request.getAssignedToId());
        }

        return customer;
    }

    public void updateEntityFromRequest(CustomerRequest request, Customer customer) {
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getCity() != null) customer.setCity(request.getCity());
        if (request.getInterestedIn() != null) customer.setInterestedIn(request.getInterestedIn());
        if (request.getRemarks() != null) customer.setRemarks(request.getRemarks());
        if (request.getStatus() != null) customer.setStatus(request.getStatus());
        if (request.getAssignedToId() != null) {
            customer.setAssignedTo(new User());
            customer.getAssignedTo().setId(request.getAssignedToId());
        }
    }
}
