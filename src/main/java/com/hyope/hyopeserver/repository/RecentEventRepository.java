package com.hyope.hyopeserver.repository;

import com.hyope.hyopeserver.entity.RecentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentEventRepository extends JpaRepository<RecentEvent, Long> {
    List<RecentEvent> findByRecipientIdAndActiveTrue(Long recipientId);
    void deleteByRecipientId(Long recipientId);
    // * ai-server의 event_id는 숫자 DB id가 아니라 어르신이 말한 근황 텍스트 그대로다(모델에게 id를
    // * 노출한 적이 없음 — agent/stages.py의 build_event_questions 참고) — 그래서 제거 요청도 text로
    // * 매칭해야 한다.
    List<RecentEvent> findByRecipientIdAndTextAndActiveTrue(Long recipientId, String text);
}