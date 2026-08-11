package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n FROM Notification n
        WHERE n.assessment.callLog.recipient.guardian.id = :guardianId
        ORDER BY n.createdAt DESC
        """)
    List<Notification> findAllByGuardianId(@Param("guardianId") Long guardianId);
}