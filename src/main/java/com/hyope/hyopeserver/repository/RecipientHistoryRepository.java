package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.RecipientHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientHistoryRepository extends JpaRepository<RecipientHistory, Long> {
    Optional<RecipientHistory> findByRecipientId(Long recipientId);
    void deleteByRecipientId(Long recipientId);
}