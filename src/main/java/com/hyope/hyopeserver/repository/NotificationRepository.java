package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}