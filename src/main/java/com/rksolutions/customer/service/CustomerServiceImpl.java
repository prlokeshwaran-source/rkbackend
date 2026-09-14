package com.rksolutions.customer.service;

import com.rksolutions.customer.dto.CustomerRequest;
import com.rksolutions.customer.dto.CustomerResponse;
import com.rksolutions.customer.dto.CustomerStatusChangeRequest;
import com.rksolutions.customer.entity.Customer;
import com.rksolutions.customer.entity.CustomerStatusHistory;
import com.rksolutions.customer.mapper.CustomerMapper;
import com.rksolutions.customer.repository.CustomerRepository;
import com.rksolutions.customer.repository.CustomerStatusHistoryRepository;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.common.exception.BadRequestException;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Customer already exists with phone: " + request.getPhone());
        }

        Customer customer = customerMapper.toEntity(request);

        User currentUser = getCurrentUser(request.getAssignedToId() != null ? request.getAssignedToId() : null);
        if (currentUser != null) {
            customer.setCreatedBy(currentUser);
        }

        customer.setStatus(request.getStatus() != null ? request.getStatus() : CustomerStatus.NEW);

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        CustomerStatus oldStatus = customer.getStatus();

        customerMapper.updateEntityFromRequest(request, customer);

        if (request.getStatus() != null && !request.getStatus().equals(oldStatus)) {
            CustomerStatusHistory history = new CustomerStatusHistory();
            history.setCustomer(customer);
            history.setOldStatus(oldStatus);
            history.setNewStatus(request.getStatus());
            history.setRemarks("Status updated via PUT");

            User changedBy = getCurrentUser(null);
            history.setChangedBy(changedBy);
            history.setChangedAt(LocalDateTime.now());

            statusHistoryRepository.save(history);

            if (request.getStatus() == CustomerStatus.JOINED && customer.getJoinedAt() == null) {
                customer.setJoinedAt(LocalDateTime.now());
            }
        }

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CustomerResponse> getAllCustomers(CustomerStatus status, Long assignedToId,
                                                   String city, String interestedIn, Pageable pageable) {
        Specification<Customer> spec = Specification.where((Specification<Customer>) null);

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }
        if (city != null && !city.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
        }
        if (interestedIn != null && !interestedIn.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("interestedIn")), "%" + interestedIn.toLowerCase() + "%"));
        }

        return customerRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse changeCustomerStatus(Long id, CustomerStatusChangeRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        CustomerStatus oldStatus = customer.getStatus();
        CustomerStatus newStatus = request.getStatus();

        customer.setStatus(newStatus);

        if (newStatus == CustomerStatus.JOINED && customer.getJoinedAt() == null) {
            customer.setJoinedAt(LocalDateTime.now());
        }

        CustomerStatusHistory history = new CustomerStatusHistory();
        history.setCustomer(customer);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setRemarks(request.getRemarks());

        User changedBy = getCurrentUser(request.getChangedById());
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse assignCustomer(Long id, Long assignedToId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + assignedToId));

        customer.setAssignedTo(assignedTo);

        CustomerStatusHistory history = new CustomerStatusHistory();
        history.setCustomer(customer);
        history.setOldStatus(customer.getStatus());
        history.setNewStatus(customer.getStatus());
        history.setRemarks("Customer assigned to " + assignedTo.getName());
        history.setChangedBy(assignedTo);
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<CustomerStatusHistory> getStatusHistory(Long customerId) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return statusHistoryRepository.findByCustomerId(customerId);
    }

    private User getCurrentUser(Long userId) {
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        }
        return null;
    }
}
