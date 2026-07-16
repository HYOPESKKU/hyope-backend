package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallLog;
import com.hyope.hyopeserver.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    @Query("SELECT c FROM CallLog c WHERE c.recipient.id = :recipientId " +
            "AND (:from IS NULL OR c.startedAt >= :from) " +
            "AND (:to IS NULL OR c.startedAt <= :to) " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<CallLog> findAllByFilters(@Param("recipientId") Long recipientId,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to,
                                   @Param("status") Status status,
                                   Pageable pageable);

    List<CallLog> findByRecipientId(Long recipientId);                              // deleteAllByRecipientId에서 사용
    void deleteByRecipientId(Long recipientId);                                     // deleteAllByRecipientId에서 사용
    Optional<CallLog> findFirstByRecipientIdOrderByStartedAtDesc(Long recipientId); // 다른 곳에서 쓰일 가능성 높음
    boolean existsByRecipientIdAndStartedAtAfter(Long recipientId, LocalDateTime dateTime); // 마찬가지
}
