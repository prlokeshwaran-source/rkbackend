package com.rksolutions.audit.service;

import com.rksolutions.audit.dto.AuditLogResponse;
import com.rksolutions.audit.entity.AuditLog;
import com.rksolutions.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {

    AuditLog logAction(User user, String action, String module,
                       Long entityId, String oldValue, String newValue,
                       String ipAddress);

    Page<AuditLogResponse> getAllLogs(Pageable pageable);

    List<AuditLogResponse> getLogsByModule(String module);

    List<AuditLogResponse> getLogsByAction(String action);

    List<AuditLogResponse> getLogsByUser(Long userId);
}
