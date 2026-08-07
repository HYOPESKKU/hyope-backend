package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.entity.RecipientHistory;
import com.hyope.hyopeserver.repository.RecipientHistoryRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipientHistoryService {

    private final RecipientRepository recipientRepository;
    private final RecipientHistoryRepository recipientHistoryRepository;

    /** AI 서버로 통화 요청 보낼 때 함께 실어보낼 히스토리 조회 */
    public String readContent(Long recipientId) {
        return recipientHistoryRepository.findByRecipientId(recipientId)
                .map(RecipientHistory::getContent)
                .orElse("");
    }

    /** 통화 종료 후 AI 서버가 보내준 히스토리로 갱신 (없으면 새로 생성) */
    @Transactional
    public void upsert(Long recipientId, String content) {
        RecipientHistory history = recipientHistoryRepository.findByRecipientId(recipientId)
                .orElse(null);

        if (history == null) {
            Recipient recipient = recipientRepository.findById(recipientId)
                    .orElseThrow(() -> new IllegalArgumentException("히스토리 저장 실패: 해당 돌봄 대상자가 없습니다."));
            history = RecipientHistory.builder()
                    .recipient(recipient)
                    .content(content)
                    .updatedAt(LocalDateTime.now())
                    .build();
            recipientHistoryRepository.save(history);
            log.info("히스토리 신규 생성: recipientId={}", recipientId);
        } else {
            history.update(content);
            log.info("히스토리 갱신: recipientId={}", recipientId);
        }
    }

    @Transactional
    public void deleteByRecipientId(Long recipientId) {
        recipientHistoryRepository.deleteByRecipientId(recipientId);
    }
}