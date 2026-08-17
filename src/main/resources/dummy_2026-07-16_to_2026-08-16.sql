-- 신규 recipient(보호대상자)용 한 달치(2026-07-16 ~ 2026-08-16) 통화/평가 더미 데이터.
-- 사전 조건: /auth/register 로 guardian 생성, 로그인 후 /guardians/{id}/recipients 로 recipient 등록을 먼저 완료할 것.
-- 아래 'REPLACE_WITH_YOUR_LOGIN_ID' 를 1단계(회원가입)에서 사용한 loginId 로 바꾸고 실행하세요.
-- (동일 guardian 이 recipient 를 여러 명 등록했다면 가장 최근에 만든 recipient 를 사용합니다.)

SET @recipient_id = (
    SELECT r.id FROM recipient r
    JOIN guardian g ON r.guardian_id = g.id
    WHERE g.login_id = 'REPLACE_WITH_YOUR_LOGIN_ID'
    ORDER BY r.created_at DESC
    LIMIT 1
);

INSERT INTO call_schedule (recipient_id, scheduled_time, is_active, created_at)
VALUES (@recipient_id, '07:00:00', TRUE, NOW());

-- 이후 INSERT 들이 기존 call_log/assessment 행과 섞이지 않도록, 삽입 전 현재 MAX(id) 를 기록해둔다.
SET @start_id = (SELECT IFNULL(MAX(id), 0) FROM call_log) + 1;

-- 32일치 통화 기록 (2026-07-16 ~ 2026-08-16, 4일마다 한 번꼴로 결번)
INSERT INTO call_log (recipient_id, started_at, ended_at, duration_sec, status) VALUES
(@recipient_id, '2026-07-16 07:00:00', '2026-07-16 07:03:11', 191, 'COMPLETED'),
(@recipient_id, '2026-07-17 07:00:00', '2026-07-17 07:03:52', 232, 'COMPLETED'),
(@recipient_id, '2026-07-18 07:00:00', '2026-07-18 07:04:33', 273, 'COMPLETED'),
(@recipient_id, '2026-07-19 07:00:00', '2026-07-19 07:03:04', 184, 'COMPLETED'),
(@recipient_id, '2026-07-20 07:00:00', NULL, 0, 'MISSED'),
(@recipient_id, '2026-07-21 07:00:00', '2026-07-21 07:04:26', 266, 'COMPLETED'),
(@recipient_id, '2026-07-22 07:00:00', '2026-07-22 07:02:57', 177, 'COMPLETED'),
(@recipient_id, '2026-07-23 07:00:00', '2026-07-23 07:03:38', 218, 'COMPLETED'),
(@recipient_id, '2026-07-24 07:00:00', '2026-07-24 07:04:19', 259, 'COMPLETED'),
(@recipient_id, '2026-07-25 07:00:00', '2026-07-25 07:02:50', 170, 'COMPLETED'),
(@recipient_id, '2026-07-26 07:00:00', '2026-07-26 07:03:31', 211, 'COMPLETED'),
(@recipient_id, '2026-07-27 07:00:00', '2026-07-27 07:04:12', 252, 'COMPLETED'),
(@recipient_id, '2026-07-28 07:00:00', '2026-07-28 07:02:43', 163, 'COMPLETED'),
(@recipient_id, '2026-07-29 07:00:00', NULL, 0, 'MISSED'),
(@recipient_id, '2026-07-30 07:00:00', '2026-07-30 07:04:05', 245, 'COMPLETED'),
(@recipient_id, '2026-07-31 07:00:00', '2026-07-31 07:02:36', 156, 'COMPLETED'),
(@recipient_id, '2026-08-01 07:00:00', '2026-08-01 07:03:17', 197, 'COMPLETED'),
(@recipient_id, '2026-08-02 07:00:00', '2026-08-02 07:03:58', 238, 'COMPLETED'),
(@recipient_id, '2026-08-03 07:00:00', '2026-08-03 07:04:39', 279, 'COMPLETED'),
(@recipient_id, '2026-08-04 07:00:00', '2026-08-04 07:03:10', 190, 'COMPLETED'),
(@recipient_id, '2026-08-05 07:00:00', '2026-08-05 07:03:51', 231, 'COMPLETED'),
(@recipient_id, '2026-08-06 07:00:00', '2026-08-06 07:04:32', 272, 'COMPLETED'),
(@recipient_id, '2026-08-07 07:00:00', NULL, 0, 'MISSED'),
(@recipient_id, '2026-08-08 07:00:00', '2026-08-08 07:03:44', 224, 'COMPLETED'),
(@recipient_id, '2026-08-09 07:00:00', '2026-08-09 07:04:25', 265, 'COMPLETED'),
(@recipient_id, '2026-08-10 07:00:00', '2026-08-10 07:02:56', 176, 'COMPLETED'),
(@recipient_id, '2026-08-11 07:00:00', '2026-08-11 07:03:37', 217, 'COMPLETED'),
(@recipient_id, '2026-08-12 07:00:00', '2026-08-12 07:04:18', 258, 'COMPLETED'),
(@recipient_id, '2026-08-13 07:00:00', '2026-08-13 07:02:49', 169, 'COMPLETED'),
(@recipient_id, '2026-08-14 07:00:00', '2026-08-14 07:03:30', 210, 'COMPLETED'),
(@recipient_id, '2026-08-15 07:00:00', NULL, 0, 'MISSED'),
(@recipient_id, '2026-08-16 07:00:00', '2026-08-16 07:02:42', 162, 'COMPLETED');

-- 통화별 질문/답변 (완료된 통화에만, 이번 배치에서 삽입한 행으로 한정)
INSERT INTO call_log_entry (call_log_id, sequence, question, answer, asked_at)
SELECT id, 1, '오늘 밥 드셨나요?', '응, 먹었어.', started_at
FROM call_log
WHERE recipient_id = @recipient_id AND status = 'COMPLETED' AND id >= @start_id;

INSERT INTO call_log_entry (call_log_id, sequence, question, answer, asked_at)
SELECT id, 2, '오늘 기분 어떠세요?', '그럭저럭 괜찮아요.', started_at
FROM call_log
WHERE recipient_id = @recipient_id AND status = 'COMPLETED' AND id >= @start_id;

SET @assessment_start_id = (SELECT IFNULL(MAX(id), 0) FROM assessment) + 1;

-- 평가 지표 (완료된 통화에만, 2주 주기로 완만히 오르내리다 8/8에 HIGH 스파이크)
INSERT INTO assessment (call_log_id, measured_at, depression_score, emotional_stability, health_risk, cognitive_score, overall_risk, risk_level, summary, recommendation) VALUES
(@start_id + 0, '2026-07-16 07:04:00', 4.1, 5.9, 3.5, 6.0, 4.4, 'CAUTION', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '오늘 중 보호자 전화가 권장됩니다. 우울감, 통증, 식사 여부를 짧게 확인하세요.'),
(@start_id + 1, '2026-07-17 07:04:00', 5.0, 5.2, 4.4, 5.5, 5.2, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 2, '2026-07-18 07:04:00', 5.8, 4.5, 5.1, 5.1, 5.9, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 3, '2026-07-19 07:04:00', 6.3, 4.2, 5.6, 4.8, 6.3, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 5, '2026-07-21 07:04:00', 5.8, 4.5, 5.1, 5.1, 5.9, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 6, '2026-07-22 07:04:00', 5.0, 5.2, 4.4, 5.5, 5.2, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 7, '2026-07-23 07:04:00', 4.1, 5.9, 3.5, 6.0, 4.4, 'CAUTION', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '오늘 중 보호자 전화가 권장됩니다. 우울감, 통증, 식사 여부를 짧게 확인하세요.'),
(@start_id + 8, '2026-07-24 07:04:00', 3.1, 6.6, 2.6, 6.5, 3.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 9, '2026-07-25 07:04:00', 2.3, 7.2, 1.8, 7.0, 2.9, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 10, '2026-07-26 07:04:00', 2.0, 7.5, 1.5, 7.2, 2.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 11, '2026-07-27 07:04:00', 2.0, 7.5, 1.5, 7.2, 2.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 12, '2026-07-28 07:04:00', 2.3, 7.2, 1.8, 7.0, 2.9, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 14, '2026-07-30 07:04:00', 4.1, 5.9, 3.5, 6.0, 4.4, 'CAUTION', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '오늘 중 보호자 전화가 권장됩니다. 우울감, 통증, 식사 여부를 짧게 확인하세요.'),
(@start_id + 15, '2026-07-31 07:04:00', 5.0, 5.2, 4.4, 5.5, 5.2, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 16, '2026-08-01 07:04:00', 5.8, 4.5, 5.1, 5.1, 5.9, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 17, '2026-08-02 07:04:00', 6.3, 4.2, 5.6, 4.8, 6.3, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 18, '2026-08-03 07:04:00', 6.3, 4.2, 5.6, 4.8, 6.3, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 19, '2026-08-04 07:04:00', 5.8, 4.5, 5.1, 5.1, 5.9, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 20, '2026-08-05 07:04:00', 5.0, 5.2, 4.4, 5.5, 5.2, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 21, '2026-08-06 07:04:00', 4.1, 5.9, 3.5, 6.0, 4.4, 'CAUTION', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '오늘 중 보호자 전화가 권장됩니다. 우울감, 통증, 식사 여부를 짧게 확인하세요.'),
(@start_id + 23, '2026-08-08 07:04:00', 7.0, 3.5, 6.5, 4.5, 7.8, 'HIGH', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '즉시 연락하고 응답이 없거나 급성 증상이 의심되면 긴급 대응을 검토하세요.'),
(@start_id + 24, '2026-08-09 07:04:00', 2.0, 7.5, 1.5, 7.2, 2.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 25, '2026-08-10 07:04:00', 2.0, 7.5, 1.5, 7.2, 2.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 26, '2026-08-11 07:04:00', 2.3, 7.2, 1.8, 7.0, 2.9, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 27, '2026-08-12 07:04:00', 3.1, 6.6, 2.6, 6.5, 3.6, 'OBSERVE', '전반적으로 안정적인 상태로 특이사항은 없었습니다.', '정상 범위입니다. 다음 통화에서 식사·복약 여부를 다시 확인하세요.'),
(@start_id + 28, '2026-08-13 07:04:00', 4.1, 5.9, 3.5, 6.0, 4.4, 'CAUTION', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '오늘 중 보호자 전화가 권장됩니다. 우울감, 통증, 식사 여부를 짧게 확인하세요.'),
(@start_id + 29, '2026-08-14 07:04:00', 5.0, 5.2, 4.4, 5.5, 5.2, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.'),
(@start_id + 31, '2026-08-16 07:04:00', 6.3, 4.2, 5.6, 4.8, 6.3, 'WARNING', '평소보다 기운이 없어 보이셨고, 식사 여부를 확인했습니다.', '전화 후 상태가 좋지 않으면 방문을 고려하세요. 돌봄 공백 여부를 확인하세요.');

-- 이행기록 (아침 식사/복약, 이번 배치에서 삽입한 assessment 행으로 한정)
INSERT INTO adherence_record (assessment_id, adherence_type, slot, is_needed, is_done)
SELECT id, 'MEAL', 'BREAKFAST', TRUE, (id % 5 <> 0) FROM assessment WHERE id >= @assessment_start_id;
INSERT INTO adherence_record (assessment_id, adherence_type, slot, is_needed, is_done)
SELECT id, 'MEDICATION', 'BREAKFAST', TRUE, (id % 4 <> 0) FROM assessment WHERE id >= @assessment_start_id;

-- 취미
INSERT INTO hobby (recipient_id, text, created_at) VALUES
(@recipient_id, '화분 가꾸기', NOW()),
(@recipient_id, '뜨개질', NOW());

-- 최근 근황 (활성 상태)
INSERT INTO recent_event (recipient_id, text, added_at, active, removed_at, removed_reason) VALUES
(@recipient_id, '동네 마트에 다녀온 일', '2026-08-10', TRUE, NULL, NULL);
