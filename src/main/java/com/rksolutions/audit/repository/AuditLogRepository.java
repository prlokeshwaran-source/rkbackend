package com.rksolutions.audit.repository;

import com.rksolutions.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByModule(String module);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByUserId(Long userId);

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
