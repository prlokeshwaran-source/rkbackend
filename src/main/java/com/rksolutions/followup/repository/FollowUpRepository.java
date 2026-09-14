package com.rksolutions.followup.repository;

import com.rksolutions.followup.entity.FollowUp;
import com.rksolutions.common.enums.FollowUpStatus;
import com.rksolutions.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    List<FollowUp> findByAssignedToAndStatus(User user, FollowUpStatus status);

    Page<FollowUp> findByAssignedToAndStatus(User user, FollowUpStatus status, Pageable pageable);

    List<FollowUp> findByScheduledAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, FollowUpStatus status);

    List<FollowUp> findByCreatedBy(User user);
}
