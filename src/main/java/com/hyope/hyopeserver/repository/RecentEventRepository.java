package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.RecentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentEventRepository extends JpaRepository<RecentEvent, Long> {
    List<RecentEvent> findByRecipientIdAndActiveTrue(Long recipientId);
    void deleteByRecipientId(Long recipientId);
}