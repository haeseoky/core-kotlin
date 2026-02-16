# Development Guide

이 문서는 Core Kotlin 프로젝트의 개발 환경 설정 및 개발 가이드라인을 설명합니다.

## 🛠️ 개발 환경

### 필수 요구사항

| 도구 | 버전 | 설명 |
|------|------|------|
| JDK | 21+ | Java Development Kit |
| Gradle | 8.14+ | Build Tool |
| Docker | Latest | Redis, Kafka, MongoDB 실행용 |
| IntelliJ IDEA | Latest | (권장) Kotlin 개발 IDE |

### 환경 설정

```bash
# JDK 21 설치 확인
java -version

# Gradle Wrapper 확인
./gradlew --version

# Docker 실행 확인
docker ps
```

## 📦 Gradle 명령어

### 빌드 관련

| 명령어 | 설명 |
|--------|------|
| `./gradlew build` | 전체 프로젝트 빌드 및 테스트 |
| `./gradlew clean` | 빌드 산출물 삭제 |
| `./gradlew :core-domain:build` | 특정 모듈 빌드 |
| `./gradlew :core-presentation:bootRun` | 애플리케이션 실행 |

### 테스트 관련

| 명령어 | 설명 |
|--------|------|
| `./gradlew test` | 전체 테스트 실행 |
| `./gradlew :core-infra:test` | 특정 모듈 테스트 |
| `./gradlew test --tests "*MemberTest"` | 패턴 매칭 테스트 |
| `./gradlew check` | 빌드 + 테스트 + 코드 검사 |

### 코드 품질

| 명령어 | 설명 |
|--------|------|
| `./gradlew ktlintCheck` | Kotlin 코드 스타일 검사 |
| `./gradlew ktlintFormat` | 코드 자동 포맷팅 |

## 🏗️ 모듈별 개발 가이드

### Domain Layer 개발

**원칙:**
- 순수 Kotlin 코드 (Spring 의존 없음)
- 불변성 보장 (data class, value class)
- 비즈니스 규칙 포함

```kotlin
@JvmInline
value class Email(private val value: String) {
    init {
        require(EMAIL_REGEX.matches(value)) { "Invalid email format" }
    }
    fun value(): String = value

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
        fun of(value: String): Email = Email(value)
    }
}
```

### Application Layer 개발

**원칙:**
- @Service로 빈 등록
- @Transactional로 트랜잭션 관리
- Command/Query 분리 (CQRS)

```kotlin
@Service
@Transactional
class MemberCommandService(
    private val memberRepository: MemberRepository,
    private val eventPublisher: KafkaProducerService
) {
    fun createMember(email: String, name: String): Member {
        val member = Member.create(
            email = Email.of(email),
            name = name
        )
        val saved = memberRepository.save(member)
        eventPublisher.send("member-events", saved.id.value(), MemberCreatedEvent(saved))
        return saved
    }
}
```

### Infra Layer 개발

**원칙:**
- Entity ↔ Domain Model 변환
- 외부 서비스와의 통신
- @ConditionalOnProperty로 환경 분리

```kotlin
@Entity
@Table(name = "members")
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var email: String = "",
    var name: String = "",
    // ...
) {
    fun toDomain(): Member = Member.restore(...)

    companion object {
        fun fromDomain(member: Member): MemberEntity = ...
    }
}
```

### Presentation Layer 개발

**원칙:**
- Controller는 DTO만 처리
- 검증은 @Valid 사용
- Service 직접 호출

```kotlin
@RestController
@RequestMapping("/api/members")
class MemberController(
    private val commandService: MemberCommandService,
    private val queryService: MemberQueryService
) {
    @PostMapping
    fun createMember(@Valid @RequestBody request: MemberRequest): ResponseEntity<MemberResponse> {
        val member = commandService.createMember(request.email, request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(member.toResponse())
    }
}
```

## 🧪 테스트 작성 가이드

### Domain Layer 테스트

```kotlin
class EmailTest {
    @Test
    fun `valid email creates Email`() {
        val email = Email.of("test@example.com")
        assertThat(email.value()).isEqualTo("test@example.com")
    }

    @Test
    fun `invalid email throws exception`() {
        assertThatThrownBy {
            Email.of("invalid")
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
```

### Repository 테스트

```kotlin
@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private lateinit var jpaRepository: MemberJpaRepository

    @Test
    fun `save and find member`() {
        val entity = MemberEntity(email = "test@example.com", name = "Test")
        jpaRepository.save(entity)

        val found = jpaRepository.findByEmail("test@example.com")
        assertThat(found).isNotNull()
    }
}
```

### Controller 테스트

```kotlin
@WebMvcTest(MemberController::class)
class MemberControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var commandService: MemberCommandService

    @Test
    fun `create member returns 201`() {
        mockMvc.post("/api/members") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"test@example.com","name":"Test"}"""
        }.andExpect {
            status { isCreated() }
        }
    }
}
```

## 🔧 외부 서비스 설정

### Docker Compose (선택사항)

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: member_db
      POSTGRES_USER: member
      POSTGRES_PASSWORD: password

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  kafka:
    image: bitnami/kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_CFG_ZOOKEEPER_CONNECT: zookeeper:2181

  mongodb:
    image: mongo:8
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: root
```

## 📝 코딩 규칙

### Kotlin 스타일

```kotlin
// ✅ 좋은 예
@Service
class MemberService(
    private val repository: MemberRepository,
    private val publisher: EventPublisher
) {
    fun getMember(id: MemberId): Member? =
        repository.findById(id)
}

// ❌ 나쁜 예
@Service
class MemberService {
    lateinit var repository: MemberRepository
    lateinit var publisher: EventPublisher

    fun getMember(id: MemberId): Member? {
        return repository.findById(id)
    }
}
```

### 명명 규칙

| 타입 | 규칙 | 예시 |
|------|------|------|
| Class | PascalCase | `MemberService` |
| Function | camelCase | `getMemberById` |
| Constant | UPPER_SNAKE_CASE | `MAX_EMAIL_LENGTH` |
| Private | camelCase | `internalValidate` |

## 🚨 공통 이슈 및 해결

### 1. 테스트에서 MongoDB 연결 실패

```kotlin
// 테스트 설정에서 제외
@TestPropertySource(properties = [
    "infra.mongo.enabled=false"
])
```

### 2. Entity 순환 참조

```kotlin
// @OneToMany 사용 시 JsonManagedReference/@JsonBackReference
// 또는 DTO로 변환 후 반환
```

### 3. 트랜잭션 롤백 안 됨

```kotlin
// @Transactional 애노테이션 확인
// 테스트에서는 @TransactionalDomains 기본 롤백
```

## 📚 참고 자료

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
