package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByCallLog_Recipient_Id(Long recipientId);
    void deleteByCallLog_Recipient_Id(Long recipientId);
    boolean existsByCallLogId(Long callLogId);
    void deleteByCallLogId(Long callLogId);
    Optional<Assessment> findByCallLogId(Long callLogId);  // 추가
}
