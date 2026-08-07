package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.aicallresult.AiCallResult;
import com.hyope.hyopeserver.dto.profile.ProfileDto;
import com.hyope.hyopeserver.dto.profile.RecentEventDto;
import com.hyope.hyopeserver.entity.Hobby;
import com.hyope.hyopeserver.entity.RecentEvent;
import com.hyope.hyopeserver.entity.Recipient;
import com.hyope.hyopeserver.repository.HobbyRepository;
import com.hyope.hyopeserver.repository.RecentEventRepository;
import com.hyope.hyopeserver.repository.RecipientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecipientProfileService {

    private final RecipientRepository recipientRepository;
    private final HobbyRepository hobbyRepository;
    private final RecentEventRepository recentEventRepository;

    /** AI 서버로 통화 요청 보낼 때 함께 실어보낼 프로필 조회 */
    public ProfileDto readProfile(Long recipientId) {
        List<String> hobbies = hobbyRepository.findByRecipientId(recipientId)
                .stream()
                .map(Hobby::getText)
                .toList();

        List<RecentEventDto> events = recentEventRepository.findByRecipientIdAndActiveTrue(recipientId)
                .stream()
                .map(e -> new RecentEventDto(
                        String.valueOf(e.getId()),
                        e.getText(),
                        e.getAddedAt()))
                .toList();

        return new ProfileDto(hobbies, events);
    }

    /** 통화 종료 후 AI 서버가 보내준 profile_updates 반영 */
    @Transactional
    public void applyUpdates(Long recipientId, List<AiCallResult.ProfileUpdateData> updates) {
        if (updates == null || updates.isEmpty()) return;

        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("프로필 갱신 실패: 해당 돌봄 대상자가 없습니다."));

        for (AiCallResult.ProfileUpdateData update : updates) {
            if ("add".equals(update.getAction())) {
                handleAdd(recipient, update);
            } else if ("remove".equals(update.getAction())) {
                handleRemove(update);
            } else {
                log.warn("알 수 없는 action: {}", update.getAction());
            }
        }
    }

    private void handleAdd(Recipient recipient, AiCallResult.ProfileUpdateData update) {
        if (update.getText() == null || update.getText().isBlank()) {
            log.warn("add 요청에 text가 없습니다.");
            return;
        }

        if ("hobby".equals(update.getKind())) {
            hobbyRepository.save(Hobby.builder()
                    .recipient(recipient)
                    .text(update.getText())
                    .createdAt(LocalDateTime.now())
                    .build());
            log.info("취미 추가: recipientId={}, text={}", recipient.getId(), update.getText());

        } else if ("event".equals(update.getKind())) {
            recentEventRepository.save(RecentEvent.builder()
                    .recipient(recipient)
                    .text(update.getText())
                    .addedAt(LocalDate.now())
                    .active(true)
                    .build());
            log.info("근황 추가: recipientId={}, text={}", recipient.getId(), update.getText());
        }
    }

    private void handleRemove(AiCallResult.ProfileUpdateData update) {
        if (update.getEventId() == null) {
            log.warn("remove 요청에 event_id가 없습니다.");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(update.getEventId());
        } catch (NumberFormatException e) {
            log.warn("event_id 파싱 실패: {}", update.getEventId());
            return;
        }

        if ("hobby".equals(update.getKind())) {
            hobbyRepository.deleteById(id);
            log.info("취미 제거: hobbyId={}, reason={}", id, update.getReason());

        } else if ("event".equals(update.getKind())) {
            recentEventRepository.findById(id).ifPresent(event -> {
                event.remove(update.getReason());
                log.info("근황 비활성화: eventId={}, reason={}", id, update.getReason());
            });
        }
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId) {
        hobbyRepository.deleteByRecipientId(recipientId);
        recentEventRepository.deleteByRecipientId(recipientId);
    }
}