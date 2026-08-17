package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    List<Hobby> findByRecipientId(Long recipientId);
    void deleteByRecipientId(Long recipientId);
    // * ai-server는 취미를 숫자 DB id가 아니라 어르신이 말한 텍스트 그대로로만 알고 있다(모델에게
    // * id를 노출한 적이 없음 — agent/stages.py의 build_interest_questions 참고) — 그래서 제거 요청도
    // * text로 매칭해야 한다.
    List<Hobby> findByRecipientIdAndText(Long recipientId, String text);
}