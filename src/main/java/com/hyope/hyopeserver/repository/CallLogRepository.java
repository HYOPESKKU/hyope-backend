package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {
    List<CallLog> findByRecipientId(Long recipientId);
    Page<CallLog> findByRecipientId(Long recipientId, Pageable pageable);
    List<CallLog> findByRecipientIdAndStartedAtBetween(Long recipientId, LocalDateTime from, LocalDateTime to);
    List<CallLog> findByRecipientIdAndStatus(Long recipientId, Status status);
    Optional<CallLog> findFirstByRecipientIdOrderByStartedAtDesc(Long recipientId);
    void deleteByRecipientId(Long recipientId);
    boolean existsByRecipientIdAndStartedAtAfter(Long recipientId, LocalDateTime dateTime);
}
