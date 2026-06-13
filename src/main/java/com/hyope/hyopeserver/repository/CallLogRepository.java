package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {
    List<CallLog> findByRecipientId(Long recipientId);
}
