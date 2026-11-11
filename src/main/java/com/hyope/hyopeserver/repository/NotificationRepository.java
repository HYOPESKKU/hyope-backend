package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.GuardianNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<GuardianNotification, Long> {
    List<GuardianNotification> findByGuardianIdOrderByCreatedAtDesc(Long guardianId);
}