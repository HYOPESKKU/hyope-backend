package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuardianRepository extends JpaRepository<Guardian, Long> { //<엔티티 클래스, @Id 필드 타입>
}
