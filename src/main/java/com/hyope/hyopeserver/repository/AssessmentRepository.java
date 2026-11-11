package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByCallLog_Recipient_Id(Long recipientId);
    void deleteByCallLog_Recipient_Id(Long recipientId);
    boolean existsByCallLogId(Long callLogId);
    void deleteByCallLogId(Long callLogId);
    Optional<Assessment> findByCallLogId(Long callLogId);

    // 특정 recipient의 assessment 중, 주어진 시각(measuredAt) 이전 것들을
    // measuredAt 내림차순으로 정렬해서 가장 최근 것부터 조회
    List<Assessment> findByCallLog_Recipient_IdAndMeasuredAtBeforeOrderByMeasuredAtDesc(
            Long recipientId,
            LocalDateTime measuredAt
    );
}