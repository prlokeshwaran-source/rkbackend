package com.rksolutions.approval.service;

import com.rksolutions.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserApprovalService {

    List<UserResponse> getPendingUsers();

    Page<UserResponse> getPendingUsersPaginated(Pageable pageable);

    UserResponse approveUser(Long id);

    UserResponse rejectUser(Long id, String reason);
}
