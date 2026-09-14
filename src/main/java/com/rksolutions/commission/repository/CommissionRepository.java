package com.rksolutions.commission.repository;

import com.rksolutions.commission.entity.Commission;
import com.rksolutions.common.enums.CommissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByUserId(Long userId);

    List<Commission> findByStatus(CommissionStatus status);

    List<Commission> findByUserIdAndStatus(Long userId, CommissionStatus status);
}
