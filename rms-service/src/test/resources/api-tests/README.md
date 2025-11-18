# RMS Service API Tests

이 디렉토리는 RMS Service의 REST API 엔드포인트를 테스트하기 위한 HTTP 파일들을 포함합니다.

## 사용 방법

### IntelliJ IDEA
1. `.http` 파일을 엽니다
2. 각 요청 옆의 실행 버튼(▶)을 클릭하여 실행합니다
3. 결과는 하단의 Run 탭에 표시됩니다

### VS Code
1. [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) 확장을 설치합니다
2. `.http` 파일을 엽니다
3. "Send Request" 링크를 클릭하여 실행합니다

## 테스트 파일 목록

### 1. organization.http
기관 관리 API 테스트
- 기관 생성, 조회, 수정, 삭제
- 기관 검색 (이름)
- 중복 일련번호 체크
- 유효성 검증 테스트

### 2. patient.http
환자 관리 API 테스트
- 환자 생성, 조회, 수정, 삭제
- 환자 검색 (이름, 생년월일, 성별)
- 중복 일련번호 체크
- 유효성 검증 테스트

### 3. sample.http
검체 및 검체 타입 관리 API 테스트
- 검체 타입 생성, 조회, 수정, 삭제
- 검체 생성, 조회, 수정, 삭제
- 검체 타입별 조회
- 중복 코드/일련번호 체크
- 유효성 검증 테스트

### 4. request.http
의뢰 관리 API 테스트
- 의뢰 생성, 조회, 수정, 삭제
- 기관별/환자별 의뢰 조회
- 의뢰일 범위 검색
- 의뢰 상태 변경
- 확장항목 추가 및 조회
- 복잡한 검색 조건
- 유효성 검증 테스트

### 5. extension.http
확장항목 관리 API 테스트
- 확장항목 생성, 조회, 수정, 삭제
- 코드로 조회
- 카테고리별 조회
- 일괄 생성
- 중복 코드 체크
- 유효성 검증 테스트

## 테스트 실행 전 준비사항

### 1. PostgreSQL 실행
```bash
cd /home/user/rms_claude/rms-service
docker-compose up -d postgres
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

또는 Docker로 전체 실행:
```bash
docker-compose up -d
```

### 3. 데이터베이스 초기화 확인
PostgreSQL 컨테이너가 시작될 때 `schema.sql`이 자동으로 실행되어 테이블과 샘플 데이터가 생성됩니다.

## 테스트 시나리오

### 정상 플로우 테스트
1. **기관 생성** → organization.http의 테스트 1 실행
2. **환자 생성** → patient.http의 테스트 1 실행
3. **검체 타입 생성** → sample.http의 테스트 1 실행
4. **검체 생성** → sample.http의 테스트 7 실행
5. **확장항목 생성** → extension.http의 테스트 1 실행
6. **의뢰 생성** → request.http의 테스트 1 실행 (위에서 생성한 ID들 사용)
7. **의뢰 조회** → request.http의 테스트 3 실행

### 예외 처리 테스트
1. **필수값 누락** → 각 파일의 Validation Error 테스트 실행
2. **중복 데이터** → 각 파일의 Duplicate Error 테스트 실행
3. **존재하지 않는 리소스** → 각 파일의 Not Found 테스트 실행
4. **잘못된 데이터 형식** → patient.http의 Invalid 테스트들 실행

### 검색 기능 테스트
1. **기관 검색** → organization.http의 테스트 4 실행
2. **환자 검색** → patient.http의 테스트 4, 5, 6 실행
3. **의뢰 검색** → request.http의 테스트 4, 5, 6, 17 실행

## 응답 코드

- `200 OK` - 조회/수정 성공
- `201 Created` - 생성 성공
- `204 No Content` - 삭제 성공
- `400 Bad Request` - 유효성 검증 실패
- `404 Not Found` - 리소스를 찾을 수 없음
- `409 Conflict` - 중복 데이터
- `500 Internal Server Error` - 서버 오류

## 참고사항

- 모든 테스트는 `@baseUrl = http://localhost:8080`을 기본으로 사용합니다
- 테스트 데이터의 ID는 실제 생성된 ID로 변경하여 사용해야 합니다
- 일부 테스트는 순서대로 실행해야 합니다 (예: 생성 후 조회)
- 각 테스트는 독립적으로 실행 가능하도록 설계되었습니다
