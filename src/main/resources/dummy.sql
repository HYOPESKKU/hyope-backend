-- password_hash는 '12345'의 BCrypt 해시입니다 (로그인 API가 BCrypt로 비교하므로 평문은 사용 불가).
INSERT INTO guardian (login_id, password_hash, first_name, last_name, birth_date, gender, phone_number, email, created_at)
VALUES ('user1', '$2b$10$vZDKhvxPVG5uZwwzpWUqeuyGfhFCqrglEpzle/wzotlLL7tYqA3om', 'JIN A', 'PARK', '2004-10-13', 'FEMALE', '01012345678', 'example@gmail.com', NOW());

INSERT INTO recipient (guardian_id, first_name, last_name, birth_date, gender, phone_number, relation_to_guardian, created_at)
VALUES (1, 'GIL DONG', 'HONG', '1973-12-03', 'FEMALE', '01013572468', 'PARENT', NOW());

INSERT INTO call_schedule (recipient_id, scheduled_time, is_active, created_at)
VALUES (1, '07:00:00', TRUE, NOW());
INSERT INTO call_schedule (recipient_id, scheduled_time, is_active, created_at)
VALUES (1, '12:00:00', TRUE, NOW());
INSERT INTO call_schedule (recipient_id, scheduled_time, is_active, created_at)
VALUES (1, '18:00:00', TRUE, NOW());

INSERT INTO call_log (recipient_id, started_at, ended_at, duration_sec, status)
VALUES (1, '2026-07-22 07:00:00', '2026-07-22 07:03:00', 180, 'COMPLETED');

INSERT INTO call_log_entry (call_log_id, sequence, question, answer, asked_at)
VALUES (1, 1, '오늘 밥 드셨나요?', '응, 먹었어.', '2026-07-22 07:00:00');
INSERT INTO call_log_entry (call_log_id, sequence, question, answer, asked_at)
VALUES (1, 2, '오늘 약 드셨나요?', '아니, 안 먹었어.', '2026-07-22 07:00:30');
INSERT INTO call_log_entry (call_log_id, sequence, question, answer, asked_at)
VALUES (1, 3, '오늘 기분 어떠세요?', '일찍 일어났더니 피곤해서 기분이 좀 안 좋은 거 같아.', '2026-07-22 07:01:30');

INSERT INTO assessment (call_log_id, measured_at, depression_score, emotional_stability, health_risk, cognitive_score, overall_risk, risk_level, summary, recommendation)
VALUES (1, '2026-07-22 07:04:00', 2.0, 3.0, 1.0, 5.5, 5.0, 'CAUTION', '전체적으로 양호한 상태이나, 지속적인 관찰이 필요해보인다.', '상담/대화 봇 통화량을 늘리거나 방 봉사 서비스 추천');

INSERT INTO adherence_record (assessment_id, adherence_type, slot, is_needed, is_done)
VALUES (1, 'MEAL', 'BREAKFAST', TRUE, TRUE);
INSERT INTO adherence_record (assessment_id, adherence_type, slot, is_needed, is_done)
VALUES (1, 'MEDICATION', 'BREAKFAST', FALSE, FALSE);