# Architecture

이 문서는 Core Kotlin 프로젝트의 아키텍처 설계 원칙과 구조를 설명합니다.

## 🎯 설계 원칙

### DDD (Domain-Driven Design)

도메인 중심 설계를 통해 비즈니스 로직을 기술 구현으로부터 분리합니다.

### 의존성 역전 원칙 (DIP)

상위 계층이 하위 계층에 의존하지 않고, 추상화에 의존합니다.

### 관심사의 분리 (Separation of Concerns)

각 계층은 자신의 관심사에만 집중합니다.

## 📦 계층별 상세

### 1. Domain Layer (core-domain)

**의존성:** 없음 (순수 Kotlin)

**역할:**
- 도메인 모델 정의
- 비즈니스 규칙 구현
- Value Object, Aggregate Root

**주요 컴포넌트:**
```
core-domain/
├── model/
│   ├── Member.kt              # Aggregate Root
│   ├── MemberId.kt            # Value Object (ID)
│   └── valueobject/
│       ├── Email.kt           # Value Object
│       └── MemberStatus.kt    # Enum
```

**제약사항:**
- Spring Framework 의존 금지
- 외부 라이브러리 의존 최소화
- 순수 비즈니스 로직만 포함

### 2. Application Layer (core-application)

**의존성:** domain

**역할:**
- 유스케이스 구현
- 트랜잭션 관리
- 도메인 조합

**주요 컴포넌트:**
```
core-application/
├── service/
│   ├── MemberCommandService.kt    # 명령(쓰기) 서비스
│   └── MemberQueryService.kt      # 조회(읽기) 서비스
└── port/
    └── (인터페이스 - 추후 구현)
```

**CQRS 패턴:**
- CommandService: 생성, 수정, 삭제
- QueryService: 조회 전용

### 3. Infrastructure Layer (core-infra)

**의존성:** domain

**역할:**
- 영속성 구현
- 외부 시스템 연동
- 기술적 세부사항

**주요 컴포넌트:**
```
core-infra/
├── persistence/
│   ├── MemberEntity.kt           # JPA Entity
│   └── MemberJpaRepository.kt    # Spring Data JPA
├── config/
│   ├── RedisConfig.kt            # Redis 설정
│   ├── KafkaConfig.kt            # Kafka 설정
│   └── MongoConfig.kt            # MongoDB 설정
├── redis/
│   └── RedisService.kt           # 캐싱 서비스
├── kafka/
│   ├── KafkaProducerService.kt   # 이벤트 발행
│   └── KafkaConsumerService.kt   # 이벤트 구독
└── mongo/
    ├── EventDocument.kt          # 이벤트 저장
    └── EventRepository.kt        # MongoDB Repository
```

### 4. Presentation Layer (core-presentation)

**의존성:** application

**역할:**
- HTTP 요청/응답 처리
- DTO 변환
- 검증

**주요 컴포넌트:**
```
core-presentation/
├── controller/
│   └── MemberController.kt       # REST Controller
└── dto/
    ├── MemberRequest.kt          # 요청 DTO
    ├── MemberResponse.kt         # 응답 DTO
    └── MemberStatusUpdateRequest.kt
```

## 🔄 데이터 흐름

```
┌──────────┐    HTTP    ┌──────────┐    Call    ┌──────────┐
│  Client  │──────────→│ Controller│──────────→│ Service  │
└──────────┘           └──────────┘           └──────────┘
                                                    │
                                                    ↓
                                              ┌──────────┐
                                              │ Domain   │
                                              │  Model   │
                                              └──────────┘
                                                    │
                           ┌──────────────────────────┼──────────────────────────┐
                           ↓                          ↓                          ↓
                    ┌──────────┐             ┌──────────┐             ┌──────────┐
                    │   JPA    │             │  Redis   │             │  Kafka   │
                    │(Postgres)│             │ (Cache)  │             │ (Event)  │
                    └──────────┘             └──────────┘             └──────────┘
```

## 🎨 패턴과 관행

### Aggregate Pattern

```
Member (Aggregate Root)
├── MemberId (Value Object)
├── Email (Value Object)
├── name (String)
├── status (MemberStatus Enum)
├── createdAt
├── updatedAt
└── deletedAt
```

### Repository Pattern

```kotlin
// Domain Port (Interface)
interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: MemberId): Member?
}

// Infrastructure Adapter
class MemberRepositoryImpl(
    private val jpaRepository: MemberJpaRepository
) : MemberRepository {
    // 구현
}
```

### Event-Driven Architecture

```
Member Created → Kafka → [Consumer Services]
Member Updated → Kafka → [Consumer Services]
Member Deleted → Kafka → [Consumer Services]
                      ↓
                 MongoDB (Event Store)
```

## 🔐 보안 고려사항

### 계층별 검증

1. **Presentation:** 입력 포맷 검증 (@Valid, @NotNull)
2. **Application:** 비즈니스 규칙 검증
3. **Domain:** 도메인 불변식 보장

### 트랜잭션 경계

- CommandService: @Transactional required
- QueryService: @Transactional(readOnly = true)

## 📈 확장성 고려사항

### 수평 확장

- Stateless 서비스
- Redis 세션/캐시
- Kafka 이벤트 기반 통신

### 모듈 독립성

- 각 모듈은 독립적으로 배포 가능
- 인터페이스 기반 통신

## 🔄 진화 계획

1. **Phase 1:** 기본 CRUD 구현 ✅
2. **Phase 2:** CQRS 도입 ✅
3. **Phase 3:** 이벤트 기반 아키텍처 ✅
4. **Phase 4:** 마이크로서비스 분리 (예정)
