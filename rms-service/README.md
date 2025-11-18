# RMS Service

차세대 AILIS 시스템의 RMS(Request Management Service) 마이크로서비스입니다.

## 📋 프로젝트 개요

RMS Service는 검사 의뢰 관리를 담당하는 핵심 서비스로, Clean Architecture 원칙을 따라 설계되었습니다.

## 🏗️ 기술 스택

- **Language**: Kotlin 2.1.0
- **JDK**: 21
- **Framework**: Spring Boot 3.5.5
- **Reactive**: Spring WebFlux + Kotlin Coroutines
- **Database**: PostgreSQL with R2DBC
- **Query DSL**: jOOQ 3.19.15
- **Validation**: Jakarta Validation
- **Serialization**: Jackson with Kotlin Module

## 🎯 주요 기능

- ✅ **의뢰 관리**: 검사 의뢰 생성, 조회, 수정, 삭제
- ✅ **기관 관리**: 의료 기관 정보 관리
- ✅ **환자 관리**: 환자 정보 관리
- ✅ **샘플 관리**: 검사 샘플 및 샘플 유형 관리
- ✅ **서비스 관리**: 검사 서비스 카탈로그 관리
- ✅ **추가 정보 관리**: 의뢰별 확장 가능한 추가 정보 관리
- ✅ **다국어 지원**: i18n을 통한 한국어/영어 메시지 지원

## 📁 프로젝트 구조

```
rms-service/
├── src/main/
│   ├── kotlin/com/idrsys/ailis/rms/
│   │   ├── domain/                    # 도메인 계층
│   │   │   ├── model/                 # 도메인 모델
│   │   │   │   ├── Request.kt
│   │   │   │   ├── Organization.kt
│   │   │   │   ├── Patient.kt
│   │   │   │   ├── Sample.kt
│   │   │   │   ├── Service.kt
│   │   │   │   └── Extension.kt
│   │   │   ├── repository/            # Repository 인터페이스
│   │   │   │   ├── RequestRepository.kt
│   │   │   │   ├── OrganizationRepository.kt
│   │   │   │   ├── PatientRepository.kt
│   │   │   │   ├── SampleRepository.kt
│   │   │   │   ├── ServiceRepository.kt
│   │   │   │   └── ExtensionRepository.kt
│   │   │   └── service/               # 도메인 서비스
│   │   ├── application/               # 애플리케이션 계층
│   │   │   ├── dto/
│   │   │   │   ├── request/           # Request DTO (Commands)
│   │   │   │   │   ├── RequestCommands.kt
│   │   │   │   │   ├── OrganizationCommands.kt
│   │   │   │   │   ├── PatientCommands.kt
│   │   │   │   │   ├── SampleCommands.kt
│   │   │   │   │   └── ExtensionCommands.kt
│   │   │   │   └── response/          # Response DTO
│   │   │   │       ├── RequestResponses.kt
│   │   │   │       ├── OrganizationResponses.kt
│   │   │   │       ├── PatientResponses.kt
│   │   │   │       ├── SampleResponses.kt
│   │   │   │       ├── ServiceResponses.kt
│   │   │   │       ├── ExtensionResponses.kt
│   │   │   │       └── CommonResponses.kt
│   │   │   ├── service/               # Application Service
│   │   │   ├── usecase/               # Use Case 인터페이스
│   │   │   └── required/              # 외부 의존성 인터페이스
│   │   ├── adapter/                   # 어댑터 계층
│   │   │   ├── persistence/           # R2DBC + jOOQ 구현
│   │   │   ├── repository/            # Repository 구현
│   │   │   ├── web/                   # REST Controllers
│   │   │   │   └── handler/
│   │   │   │       └── GlobalExceptionHandler.kt
│   │   │   └── external/              # 외부 서비스 클라이언트
│   │   ├── infrastructure/            # 인프라 계층
│   │   │   └── config/
│   │   │       ├── MessageConfig.kt
│   │   │       ├── R2dbcConfig.kt
│   │   │       └── WebFluxConfig.kt
│   │   ├── shared/                    # 공유 계층
│   │   │   ├── exception/             # Exception 정의
│   │   │   │   ├── BaseException.kt
│   │   │   │   ├── RequestExceptions.kt
│   │   │   │   ├── OrganizationExceptions.kt
│   │   │   │   ├── PatientExceptions.kt
│   │   │   │   ├── SampleExceptions.kt
│   │   │   │   ├── ServiceExceptions.kt
│   │   │   │   ├── ExtensionExceptions.kt
│   │   │   │   ├── UserExceptions.kt
│   │   │   │   └── DatabaseExceptions.kt
│   │   │   ├── util/                  # 유틸리티
│   │   │   └── constant/              # 상수
│   │   └── RmsServiceApplication.kt
│   └── resources/
│       ├── messages/                   # i18n 메시지
│       │   ├── messages.properties     # 기본 (한국어)
│       │   ├── messages_ko.properties  # 한국어
│       │   └── messages_en.properties  # 영어
│       ├── application.yml
│       └── application-local.yml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🏛️ Clean Architecture

이 프로젝트는 Clean Architecture 원칙을 따릅니다:

### 의존성 규칙
```
Domain ← Application ← Adapter ← Infrastructure
```

- **Domain**: 비즈니스 로직과 엔티티, 프레임워크 독립적
- **Application**: 유즈케이스 구현, 도메인 로직 조합
- **Adapter**: 외부 인터페이스 (Web, DB, External API)
- **Infrastructure**: 프레임워크 설정

### 계층별 책임

#### Domain Layer
- **Model**: 비즈니스 엔티티 및 Value Object
- **Repository**: 데이터 접근 인터페이스 (Port)
- **Service**: 도메인 서비스 (복잡한 비즈니스 로직)

#### Application Layer
- **DTO**: Request/Response 데이터 전송 객체
- **Service**: 애플리케이션 서비스 (유즈케이스 구현)
- **UseCase**: 유즈케이스 인터페이스
- **Required**: 외부 의존성 인터페이스 (Outbound Port)

#### Adapter Layer
- **Persistence**: R2DBC + jOOQ 구현
- **Repository**: Repository 구현체 (Adapter)
- **Web**: REST API Controller
- **External**: 외부 서비스 클라이언트

#### Infrastructure Layer
- **Config**: Spring 설정 클래스들

## 🌍 다국어 지원 (i18n)

### 특징
- ✅ 기본 언어: **한국어**
- ✅ 지원 언어: 한국어(ko), 영어(en)
- ✅ Validation 메시지도 i18n 지원
- ✅ Exception 메시지 다국어 지원

### 메시지 파일 위치
```
src/main/resources/messages/
├── messages.properties        # 기본 (한국어)
├── messages_ko.properties     # 한국어
└── messages_en.properties     # 영어
```

### 메시지 사용 예시

**Validation 메시지**
```kotlin
@field:NotBlank(message = "{validation.notBlank}")
val name: String
```

**Exception 메시지**
```kotlin
class RequestNotFoundException(requestId: String) :
    NotFoundException("exception.notFound.request.id", requestId)
```

## 🚨 Exception 처리

### Exception 계층 구조
```
BaseException (sealed class)
├── NotFoundException (404)
├── DuplicateException (409)
├── ValidationException (422)
├── AuthenticationException (401)
├── AuthorizationException (403)
├── BadRequestException (400)
└── DatabaseException (409)
```

### 도메인별 Exception
- **Request**: RequestNotFoundException, DuplicateRequestException 등
- **Organization**: OrganizationNotFoundException, DuplicateOrganizationException 등
- **Patient**: PatientNotFoundException, DuplicatePatientException 등
- **Sample**: SampleNotFoundException, SampleTypeNotFoundException 등
- **Service**: ServiceNotFoundException, DuplicateServiceException 등
- **Extension**: ExtensionNotFoundException, DuplicateExtensionException 등
- **User**: UserNotFoundException, AdminAuthorizationException 등
- **Database**: UniqueKeyViolationException, ForeignKeyViolationException 등

### GlobalExceptionHandler
모든 Exception을 catch하여 일관된 형식의 에러 응답을 반환합니다.

**에러 응답 형식**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "요청한 의뢰를 찾을 수 없습니다",
  "path": "/api/requests/123",
  "timestamp": "2025-11-18T10:30:00"
}
```

## 🔧 빌드 및 실행

### 필수 요구사항
- JDK 21
- PostgreSQL 15+
- Gradle 8.x

### 로컬 실행
```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# 실행
./gradlew bootRun

# 특정 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 데이터베이스 설정
```yaml
# application-local.yml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/rms
    username: rms_user
    password: rms_password
```

## 📊 주요 개선사항 (기존 RMS 대비)

### 1. 메시지 일관성
| 항목 | 기존 RMS | 새로운 RMS Service |
|------|----------|-------------------|
| Validation 메시지 | 영문/한국어 혼재 | i18n 통일 (한국어 기본) |
| Exception 메시지 | 하드코딩 | MessageSource 기반 |
| 다국어 지원 | 없음 | 한국어/영어 지원 |

### 2. Exception 처리
| 항목 | 기존 RMS | 새로운 RMS Service |
|------|----------|-------------------|
| Exception 구조 | RuntimeException 직접 사용 | BaseException 계층 구조 |
| 에러 응답 | 비표준 | 표준화된 ErrorResponse |
| HTTP 상태 코드 | 불일치 | Exception별 명확한 코드 |

### 3. Architecture
| 항목 | 기존 RMS | 새로운 RMS Service |
|------|----------|-------------------|
| 아키텍처 | Layered | Clean Architecture |
| 의존성 관리 | 순환 참조 가능 | 의존성 역전 원칙 |
| 테스트 용이성 | 낮음 | 높음 (Port/Adapter) |

### 4. 코드 품질
- ✅ Kotlin data class 활용
- ✅ Immutable 객체 사용
- ✅ Null Safety
- ✅ Validation 어노테이션 통일
- ✅ 한국어 주석 및 문서화

## 🎨 코딩 컨벤션

### 언어 사용
- **코드**: Kotlin
- **주석**: 한국어
- **Commit 메시지**: 한국어
- **API 문서**: 한국어

### Naming Convention
```kotlin
// Domain Model: 명사
data class Request(...)

// Repository: 도메인명 + Repository
interface RequestRepository

// Command: 동사 + 도메인명 + Command
data class CreateRequestCommand(...)

// Response: 도메인명 + Response
data class RequestResponse(...)

// Exception: 도메인명 + 예외유형 + Exception
class RequestNotFoundException(...)
```

## 📝 다음 단계

### 구현 필요 항목
1. Application Service 구현
2. Repository 구현체 (R2DBC + jOOQ)
3. REST Controller 구현
4. 데이터베이스 스키마 정의
5. 테스트 코드 작성
6. API 문서 (Swagger/OpenAPI)
7. Docker 설정
8. CI/CD 파이프라인

## 📚 참고 자료

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [R2DBC](https://r2dbc.io/)
- [jOOQ](https://www.jooq.org/)

## 👥 기여자

AILIS Development Team

## 📄 라이선스

Proprietary - All Rights Reserved
