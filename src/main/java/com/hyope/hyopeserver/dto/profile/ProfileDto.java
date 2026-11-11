package com.hyope.hyopeserver.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ProfileDto(
        List<String> hobbies,
        @JsonProperty("recent_events") List<RecentEventDto> recentEvents,
        String address,
        // ai-server(agent/brain.py의 write_call_result_outbox)가 통화 종료 시 보호자에게 보낼
        // 요약 SMS의 수신 번호/호칭으로 이 두 필드를 읽는다 — guardian이 없으면 null로 보내고,
        // ai-server 쪽은 null이면 그냥 SMS 발송을 건너뛴다(강제 아님).
        @JsonProperty("guardian_phone_number") String guardianPhoneNumber,
        @JsonProperty("recipient_name") String recipientName
) {}