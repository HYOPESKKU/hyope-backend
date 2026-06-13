package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    List<Recipient> findByGuardianId(Long guardianId); //JPA가 자동으로 WHERE guardian_id = ? 쿼리를 만듬
}
