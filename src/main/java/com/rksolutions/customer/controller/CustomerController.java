package com.rksolutions.customer.controller;

import com.rksolutions.customer.dto.CustomerRequest;
import com.rksolutions.customer.dto.CustomerResponse;
import com.rksolutions.customer.dto.CustomerStatusChangeRequest;
import com.rksolutions.customer.entity.CustomerStatusHistory;
import com.rksolutions.customer.mapper.CustomerMapper;
import com.rksolutions.customer.service.CustomerService;
import com.rksolutions.common.enums.CustomerStatus;
import com.rksolutions.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.ok(ApiResponse.created(response, "Customer created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String interestedIn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<CustomerResponse> customers = customerService.getAllCustomers(
                status, assignedToId, city, interestedIn, pageable);

        return ResponseEntity.ok(ApiResponse.success(customers, "Customers retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer retrieved successfully"));
    }

    @GetMapping("/{id}/status-history")
    public ResponseEntity<ApiResponse<List<CustomerStatusHistory>>> getStatusHistory(@PathVariable Long id) {
        List<CustomerStatusHistory> history = customerService.getStatusHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history, "Status history retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CustomerResponse>> changeCustomerStatus(
            @PathVariable Long id,
            @Valid @RequestBody CustomerStatusChangeRequest request) {
        CustomerResponse response = customerService.changeCustomerStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer status updated successfully"));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<CustomerResponse>> assignCustomer(
            @PathVariable Long id,
            @RequestParam Long assignedToId) {
        CustomerResponse response = customerService.assignCustomer(id, assignedToId);
        return ResponseEntity.ok(ApiResponse.success(response, "Customer assigned successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Customer deleted successfully"));
    }
}
