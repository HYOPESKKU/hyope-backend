🐧 로컬로 백엔드 돌릴 때 필요한 것들 정리해놨습니다~ 나중에 테스트해야 할 때 참고하세요

## 로컬 테스트 가이드

---

### 1. Java 설치

**Mac**
```bash
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Windows**
1. [https://adoptium.net](https://adoptium.net) 접속
2. **Temurin 17 (LTS)** 다운로드 후 설치
3. 설치 시 **"Add to PATH"** 옵션 체크

설치 확인:
```bash
java -version
# openjdk version "17.x.x" 출력되면 성공
```

---

### 2. MySQL 설치

**Mac**
```bash
brew install mysql
brew services start mysql

# 초기 보안 설정
mysql_secure_installation
# VALIDATE PASSWORD 플러그인 → N
# root 비밀번호 설정 → 원하는 비밀번호 입력
# 나머지 → 전부 Y
```

**Windows**
1. [https://dev.mysql.com/downloads/installer](https://dev.mysql.com/downloads/installer) 접속
2. **MySQL Installer** 다운로드 후 실행
3. Setup Type → **Developer Default** 선택
4. root 비밀번호 설정 후 설치 완료

설치 확인:
```bash
mysql -u root -p
# 비밀번호 입력 후 mysql> 프롬프트 뜨면 성공
```

MySQL 접속 후 DB 및 유저 생성:
```sql
CREATE DATABASE hyope_db DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'hyope'@'localhost' IDENTIFIED BY '비밀번호';
GRANT ALL PRIVILEGES ON hyope_db.* TO 'hyope'@'localhost';
FLUSH PRIVILEGES;
exit
```

---

### 3. 프로젝트 세팅

**저장소 클론**
```bash
git clone https://github.com/HYOPESKKU/hyope-backend.git
cd hyope-backend
```

**`src/main/resources/application.properties` 수정**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hyope_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=hyope
spring.datasource.password=위에서_설정한_비밀번호
```

---

### 4. 서버 실행

> Spring Boot는 별도 설치 없이 프로젝트에 내장되어 있습니다. 아래 명령어로 바로 실행할 수 있습니다.

**Mac/Linux**
```bash
./gradlew bootRun
```

**Windows**
```bash
gradlew.bat bootRun
```

정상 실행 시 아래 로그가 출력됩니다:

Tomcat started on port 8080 (http) with context path '/'
Started HyopeServerApplication in X seconds

> `application.properties`의 `ddl-auto=create` 설정으로 서버 실행 시 테이블이 자동 생성됩니다.

---

### 5. 더미데이터 삽입

`src/main/resources/dummy.sql` 파일에 더미데이터가 작성되어 있습니다.
데이터를 수정하거나 추가하고 싶다면 해당 파일을 직접 편집하세요.

서버 실행 후 아래 명령어로 삽입합니다:

**Mac**
```bash
mysql -u hyope -p hyope_db < src/main/resources/dummy.sql
```

**Windows**
```bash
mysql -u hyope -p hyope_db < src\main\resources\dummy.sql
```

> **주의**: `ddl-auto=create`이므로 서버를 재시작하면 테이블이 초기화됩니다. 재시작 후 더미데이터를 다시 삽입해야 합니다.

현재 삽입된 더미데이터 구성:

| 테이블 | 데이터 |
|---|---|
| `guardian` | 1건 |
| `recipient` | 1건 |
| `call_schedule` | 3건 (07:00, 12:00, 18:00) |
| `call_log` | 1건 (COMPLETED) |
| `call_log_entry` | 3건 (질문-답변 세트) |
| `assessment` | 1건 (CAUTION) |
| `adherence_record` | 2건 (MEAL, MEDICATION) |

---

### 6. API 테스트

`src/main/resources/test.http` 파일에 전체 엔드포인트 테스트 케이스가 작성되어 있습니다.

**IntelliJ HTTP Client 사용 시**
- `test.http` 파일 열기
- 원하는 요청 왼쪽의 ▶ 버튼 클릭 또는 `Cmd + Enter` (Mac) / `Ctrl + Enter` (Windows)

**curl 사용 시 예시**
```bash
# 보호자 단건 조회
curl -X GET http://localhost:8080/guardian/1

# 로그인
curl -X POST http://localhost:8080/guardian/login \
  -H "Content-Type: application/json" \
  -d '{"loginId": "user1", "password": "12345"}'
```

- Base URL: `http://localhost:8080`
- 모든 요청/응답은 JSON 형식입니다.
- 상세 API 명세는 README를 참고하세요.

완료된 테스트 항목:

| 도메인 | 테스트 케이스 |
|---|---|
| Guardian | 전체 조회, 단건 조회, 생성, 수정, 로그인, 삭제 |
| Recipient | 단건 조회, 목록 조회, 생성, 수정, 삭제 |
| CallSchedule | 단건 조회, 목록 조회, 생성, 수정, 삭제 |
| CallLog | 단건 조회, 목록 조회, 기간별 조회, 상태별 조회, 생성, 삭제 |
| CallLogEntry | 단건 조회, 목록 조회, 전체 조회, 생성, 일괄 생성, 삭제 |
| Assessment | 단건 조회, 목록 조회, 생성, 삭제 |
| AdherenceRecord | 단건 조회, 목록 조회, 생성, 수정, 삭제 |