package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallLogEntryRepository extends JpaRepository<CallLogEntry, Long> {
    Page<CallLogEntry> findByCallLogId(Long CallLogId, Pageable pageable);
    List<CallLogEntry> findByCallLogIdOrderBySequenceAsc(Long callLogId);
    void deleteByCallLogId(Long callLogId);
}
