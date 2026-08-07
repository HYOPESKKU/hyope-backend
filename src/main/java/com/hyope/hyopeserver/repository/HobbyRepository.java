package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    List<Hobby> findByRecipientId(Long recipientId);
    void deleteByRecipientId(Long recipientId);
}