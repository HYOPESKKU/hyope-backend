package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.CallSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface CallScheduleRepository extends JpaRepository<CallSchedule, Long> {
    List<CallSchedule> findByRecipientId(Long recipientId);
    void deleteByRecipientId(Long recipientId);
    List<CallSchedule> findByScheduledTimeBetweenAndIsActiveTrue(LocalTime from, LocalTime to);
}
