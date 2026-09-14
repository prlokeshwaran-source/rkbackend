package com.rksolutions.audit.service;

import com.rksolutions.audit.dto.AuditLogResponse;
import com.rksolutions.audit.entity.AuditLog;
import com.rksolutions.audit.mapper.AuditLogMapper;
import com.rksolutions.audit.repository.AuditLogRepository;
import com.rksolutions.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public AuditLog logAction(User user, String action, String module,
                              Long entityId, String oldValue, String newValue, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setModule(module);
        log.setEntityId(entityId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setIpAddress(ipAddress);
        return auditLogRepository.save(log);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<AuditLogResponse> getLogsByModule(String module) {
        return auditLogRepository.findByModule(module).stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<AuditLogResponse> getLogsByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<AuditLogResponse> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}
