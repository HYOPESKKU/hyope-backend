## ERD 관계 요약

| 관계 | 카디널리티 |
|---|---|
| Guardian → Recipient | 1 : N |
| Recipient → CallSchedule | 1 : N |
| Recipient → CallLog | 1 : N |
| Recipient → Assessment | 1 : N |
| CallLog → CallLogEntry | 1 : N |
| CallLog → Assessment | 1 : 0..1 (단방향) |
| Assessment → AdherenceRecord | 1 : N |

## 테이블 명세

### guardian (보호자)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| login_id | VARCHAR | UNIQUE | 로그인 ID |
| password_hash | VARCHAR | | 비밀번호 해시 |
| first_name | VARCHAR | | 이름 |
| last_name | VARCHAR | | 성 |
| phone_number | VARCHAR | | 전화번호 |
| email | VARCHAR | | 이메일 |
| gender | ENUM | MALE, FEMALE | 성별 |
| created_at | DATETIME | | 생성일시 |

### recipient (피보호자)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| guardian_id | BIGINT | FK → guardian.id (N:1) | 보호자 |
| first_name | VARCHAR | | 이름 |
| last_name | VARCHAR | | 성 |
| birth_date | DATE | | 생년월일 |
| gender | ENUM | MALE, FEMALE | 성별 |
| phone_number | VARCHAR | | 전화번호 |
| created_at | DATETIME | | 생성일시 |

### call_schedule (전화 일정)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| recipient_id | BIGINT | FK → recipient.id (N:1) | 피보호자 |
| scheduled_time | TIME | | 예약 시간 |
| is_active | BOOLEAN | | 활성 여부 |
| created_at | DATETIME | | 생성일시 |

### call_log (전화 기록)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| recipient_id | BIGINT | FK → recipient.id (N:1) | 피보호자 |
| started_at | DATETIME | | 통화 시작 시각 |
| ended_at | DATETIME | | 통화 종료 시각 |
| duration_sec | INT | | 통화 시간(초) |
| status | ENUM | COMPLETED, FAILED, MISSED | 통화 상태 |

### call_log_entry (전화 기록 상세)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| call_log_id | BIGINT | FK → call_log.id (N:1) | 전화 기록 |
| sequence | INT | | 순서 |
| question | TEXT | | 질문 |
| answer | TEXT | | 답변 |
| asked_at | DATETIME | | 질문 시각 |

### assessment (평가)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| recipient_id | BIGINT | FK → recipient.id (N:1) | 피보호자 |
| call_log_id | BIGINT | FK → call_log.id, UNIQUE (1:0..1) | 평가 대상 전화 기록 |
| measured_at | DATETIME | | 측정 시각 |
| depression_score | FLOAT | | 우울 점수 |
| emotional_stability | FLOAT | | 감정 안정성 |
| health_risk | FLOAT | | 건강 위험도 |
| cognitive_score | FLOAT | | 인지 점수 |
| overall_risk | FLOAT | | 종합 위험도 |
| risk_level | ENUM | OBSERVE, CAUTION, WARNING, HIGH | 종합 위험도 |
| summary | TEXT | | 돌봄 대상자 상태 요약 |
| recommendation | TEXT | | 보호자 행동 권고 |

### adherence_record (식사/복약 이행 기록)

| 컬럼명 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| assessment_id | BIGINT | FK → assessment.id (N:1) | 평가 |
| adherence_type | ENUM | MEAL, MEDICATION | 이행 유형 |
| slot | ENUM | BREAKFAST, LUNCH, DINNER | 시간대 |
| is_needed | BOOLEAN | NOT NULL | 필요 여부 |
| is_done | BOOLEAN | NOT NULL | 완료 여부 |

## ERD diagram

<img width="2207" height="815" alt="Untitled-3" src="https://github.com/user-attachments/assets/73ca101d-489a-4183-827a-0d76c1082a70" />

