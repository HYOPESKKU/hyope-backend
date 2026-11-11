package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.dto.aicallresult.AiCallResult;
import com.hyope.hyopeserver.dto.profile.ProfileDto;
import com.hyope.hyopeserver.dto.profile.RecentEventDto;
import com.hyope.hyopeserver.entity.Guardian;
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

        // ai-server(integrations/dispatch.py)는 도움처 검색 시 profile.address만 읽는다 — 최상위
        // address 필드는 안 본다(AiClientService 참고). 여기서 profile 안에 넣어서 그 계약을 맞춘다.
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("프로필 조회 실패: 해당 돌봄 대상자가 없습니다."));

        // 통화 종료 요약 SMS 수신처 — EmergencyAlertService와 동일한 소스(recipient.getGuardian())를
        // 재사용한다. 위급 알림과 달리 이 값은 매 통화 요청마다 조회되는 경로라 guardian 미등록
        // 케이스를 방어적으로 처리한다(없으면 null — ai-server가 SMS 발송을 건너뜀).
        Guardian guardian = recipient.getGuardian();
        String guardianPhoneNumber = guardian != null ? guardian.getPhoneNumber() : null;
        String recipientName = "%s%s".formatted(
                recipient.getLastName() != null ? recipient.getLastName() : "",
                recipient.getFirstName() != null ? recipient.getFirstName() : "");

        return new ProfileDto(hobbies, events, recipient.getAddress(), guardianPhoneNumber, recipientName);
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
                handleRemove(recipientId, update);
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

    // * ai-server(agent/stages.py의 build_interest_questions/build_event_questions)는 취미/근황의
    // * 숫자 DB id를 모델에게 노출한 적이 없다 — 모델은 어르신이 말한 텍스트 그대로만 안다. 그래서
    // * hobby_name/event_id(둘 다 실질적으로 텍스트)로 recipient 범위 안에서 매칭해 지운다. 숫자 id
    // * 파싱을 시도하던 예전 방식은 항상 실패해서 제거 요청이 조용히 아무 효과도 없었다.
    private void handleRemove(Long recipientId, AiCallResult.ProfileUpdateData update) {
        if ("hobby".equals(update.getKind())) {
            String hobbyName = update.getHobbyName();
            if (hobbyName == null || hobbyName.isBlank()) {
                log.warn("취미 remove 요청에 hobby_name이 없습니다.");
                return;
            }
            List<Hobby> matches = hobbyRepository.findByRecipientIdAndText(recipientId, hobbyName);
            if (matches.isEmpty()) {
                log.warn("취미 제거 실패: 일치하는 항목 없음 — recipientId={}, hobby_name={}", recipientId, hobbyName);
                return;
            }
            hobbyRepository.deleteAll(matches);
            log.info("취미 제거: recipientId={}, hobby_name={}, reason={}", recipientId, hobbyName, update.getReason());

        } else if ("event".equals(update.getKind())) {
            String eventText = update.getEventId();
            if (eventText == null || eventText.isBlank()) {
                log.warn("근황 remove 요청에 event_id가 없습니다.");
                return;
            }
            List<RecentEvent> matches = recentEventRepository.findByRecipientIdAndTextAndActiveTrue(recipientId, eventText);
            if (matches.isEmpty()) {
                log.warn("근황 비활성화 실패: 일치하는 항목 없음 — recipientId={}, event_id={}", recipientId, eventText);
                return;
            }
            matches.forEach(event -> event.remove(update.getReason()));
            log.info("근황 비활성화: recipientId={}, event_id={}, reason={}", recipientId, eventText, update.getReason());
        }
    }

    @Transactional
    public void deleteAllByRecipientId(Long recipientId) {
        hobbyRepository.deleteByRecipientId(recipientId);
        recentEventRepository.deleteByRecipientId(recipientId);
    }
}