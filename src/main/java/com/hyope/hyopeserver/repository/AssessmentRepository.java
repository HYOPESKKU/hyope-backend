package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByRecipientId(Long recipientId);
    List<Assessment> fingByCallLogId(Long callLogId);
}
