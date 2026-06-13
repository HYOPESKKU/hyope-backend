package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallLogEntryRepository extends JpaRepository<CallLogEntry, Long> {
    List<CallLogEntry> findByCallLogId(Long CallLogId);
}
