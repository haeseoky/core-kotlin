# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Test Commands

```bash
# Build entire project
./gradlew build

# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core-domain:test
./gradlew :core-application:test
./gradlew :core-infra:test
./gradlew :core-presentation:test

# Run application
./gradlew :core-presentation:bootRun

# Clean build
./gradlew clean build

# Test single class (example)
./gradlew :core-domain:test --tests "MemberIdTest"
```

## Module Architecture

This is a multi-module Gradle project following Domain-Driven Design (DDD) with hexagonal architecture.

**Modules:**
- `core-domain` - Pure Kotlin, no external dependencies. Contains domain models, value objects, and port interfaces.
- `core-application` - Business logic layer. Depends on `core-domain`. Contains services that use domain ports.
- `core-infra` - Infrastructure implementations. Depends on `core-domain`. Contains JPA entities, Kafka/Redis/MongoDB adapters.
- `core-presentation` - REST API layer. Depends on `core-application`. Contains controllers and DTOs.

**Dependency Flow (critical):**
```
core-presentation → core-application → core-domain
                                      ↑
                               core-infra
```

Infra implements domain ports, allowing the domain to remain pure.

## Hexagonal Architecture (Ports & Adapters)

The project uses ports and adapters pattern for clean separation:

**Domain Ports (in `core-domain/src/main/kotlin/.../domain/port/`):**
- `MemberRepository` - Repository interface for Member aggregate
- `MemberEventPort` - Interface for publishing domain events
- `MemberEventConsumer` - Interface for consuming external events

**Infra Adapters (in `core-infra/src/main/kotlin/.../infra/adapter/`):**
- `MemberRepositoryAdapter` - Implements `MemberRepository` with JPA
- `MemberEventPublisher` - Implements `MemberEventPort` with Kafka
- `MemberEventConsumerImpl` - Implements `MemberEventConsumer` with Kafka

**Key pattern:** Application services depend on domain ports (interfaces), not infra implementations. This allows testing with mocks and swapping technologies without changing domain/application code.

## Event-Driven Architecture

Domain events are published through `MemberEventPort`:

1. Service calls business logic
2. Service publishes event via `memberEventPort.publish(event)`
3. `MemberEventPublisher` adapter sends to Kafka topic `member-events`
4. `KafkaConsumerService` receives events
5. Events are routed to `MemberEventConsumerImpl`

**Event types (in `core-domain/.../model/event/MemberEvent.kt`):**
- `MemberCreatedEvent`
- `MemberUpdatedEvent`
- `MemberStatusChangedEvent`
- `MemberDeletedEvent`

## Module-Specific Guidelines

### core-domain
- **No Spring/Framework dependencies** - Pure Kotlin only
- Value objects use `value class` for type safety (e.g., `Email`, `MemberId`)
- All business rules and invariants are enforced here

### core-application
- Services use `@Service` and `@Transactional`
- Inject domain ports (e.g., `MemberEventPort`) for cross-cutting concerns
- Do NOT import from `core-infra` package

### core-infra
- Adapters implement domain port interfaces
- Use `@Component` or `@Repository` for Spring beans
- Entity ↔ Domain Model conversion methods: `toDomain()`, `fromDomain()`

### core-presentation
- Controllers are thin - delegate to application services
- DTOs for request/response
- Do NOT bypass application layer

## Configuration Notes

**Java Version:** JDK 21 (set in `gradle.properties` as `org.gradle.java.home`)

**Test Isolation:** Tests exclude Redis/Kafka/MongoDB auto-configuration via:
- `@TestPropertySource` properties: `infra.redis.enabled=false`, `infra.kafka.enabled=false`, `infra.mongo.enabled=false`
- `@ComponentScan` excludeFilters for `adapter`, `config`, `mongo`, `redis`, `kafka` packages

**External Services (local development):**
- PostgreSQL: 5432
- Redis: 6379
- Kafka: 10000-10002 (3 brokers)
- MongoDB: 27017

## Important Files

- `gradle.properties` - Contains Java home configuration (JDK 21 path)
- `settings.gradle.kts` - Module definitions
- `src/main/kotlin/com/ocean/member/MemberApplication.kt` - Main application entry point
