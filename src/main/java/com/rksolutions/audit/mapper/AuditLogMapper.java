package com.rksolutions.audit.mapper;

import com.rksolutions.audit.dto.AuditLogResponse;
import com.rksolutions.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(AuditLog log) {
        if (log == null) return null;

        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());

        if (log.getUser() != null) {
            response.setUserId(log.getUser().getId());
            response.setUserName(log.getUser().getName());
        }

        response.setAction(log.getAction());
        response.setModule(log.getModule());
        response.setEntityId(log.getEntityId());
        response.setOldValue(log.getOldValue());
        response.setNewValue(log.getNewValue());
        response.setIpAddress(log.getIpAddress());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}
