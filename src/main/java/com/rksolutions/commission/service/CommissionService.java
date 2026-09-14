package com.rksolutions.commission.service;

import com.rksolutions.commission.dto.CommissionResponse;
import com.rksolutions.commission.dto.CommissionStatusRequest;
import com.rksolutions.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommissionService {

    List<CommissionResponse> calculateCommissionFromPayment(Long paymentId);

    CommissionResponse getCommissionById(Long id);

    List<CommissionResponse> getAllCommissions();

    List<CommissionResponse> getPendingCommissions();

    List<CommissionResponse> getCommissionsByUser(Long userId);

    Page<CommissionResponse> getAllCommissionsPaginated(Pageable pageable);

    CommissionResponse approveCommission(Long id, Long approvedById);

    CommissionResponse rejectCommission(Long id, CommissionStatusRequest request, Long approvedById);
}
