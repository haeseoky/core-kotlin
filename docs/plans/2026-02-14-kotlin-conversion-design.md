# Java to Kotlin Conversion Design

**Date:** 2026-02-14
**Project:** core-kotlin
**Source:** /Users/haeseoky/workspace/2026/core

## Overview

Convert Java-based Spring Boot member management service to Kotlin, maintaining DDD architecture and improving code quality with Kotlin language features.

## Approach

**Selected:** IntelliJ Auto-Conversion + Auto-Refactoring

- Use IntelliJ's "Convert Java to Kotlin" functionality
- Apply auto-refactoring script for Kotlin idioms
- Fastest approach with good Kotlin feature utilization

## Architecture

```
core-kotlin/
├── build.gradle.kts           # Kotlin DSL
├── settings.gradle.kts        # Kotlin DSL
├── gradle/
│   └── libs.versions.toml    # Version Catalog (new)
├── core-domain/
│   ├── build.gradle.kts       # kotlin("jvm") plugin
│   └── src/
│       ├── main/kotlin/       # .java → .kt
│       └── test/kotlin/       # Tests also converted
├── core-application/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
├── core-infra/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
└── core-presentation/
    ├── build.gradle.kts
    └── src/main/kotlin/
```

## Components

### 1. Domain Layer (core-domain)

**Conversion Targets:**
- `Member.java` → `Member.kt` (data class)
- `Email.kt`, `MemberStatus.kt` (value objects)
- `MemberRepository.kt` (interface)

**Kotlin Features:**
```kotlin
// data class for entities
data class Member(
    val id: MemberId,
    val email: Email,
    var name: String,
    var status: MemberStatus,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
)

// @JvmInline value class for value objects
@JvmInline
value class Email(val value: String)
```

### 2. Application Layer (core-application)

**Conversion Targets:**
- `MemberCommandService.kt`
- `MemberQueryService.kt`
- Port/Adapter interfaces

**Kotlin Features:**
- Extension functions for DTO mapping
- Nullable type safety (`Long?` instead of `@Nullable`)
- Coroutines for async (if needed)

### 3. Infrastructure Layer (core-infra)

**Conversion Targets:**
- `MemberEntity.kt` (JPA Entity)
- `MemberMapper.kt` (Extension functions)

**Considerations:**
- Maintain JPA annotations (`@Entity`, `@Id`, etc.)
- Handle Kotlin nullable properties with JPA columns

### 4. Presentation Layer (core-presentation)

**Conversion Targets:**
- `MemberController.kt`
- `*Request.kt`, `*Response.kt` (DTOs)

**Kotlin Features:**
- data class for DTOs
- Field-level annotations (`@field:NotNull`)

## Data Flow

```
Client Request
    ↓
Controller (presentation) - validates @Valid
    ↓
DTO → Domain mapping (extension functions)
    ↓
Service (application) - @Transactional
    ↓
Domain Business Logic
    ↓
Repository.save() → Entity mapping
    ↓
JPA Persistence → Database
```

### Kotlin Data Flow Changes

**DTO → Domain mapping:**
```kotlin
fun MemberRequest.toDomain(): Member =
    Member.create(Email.of(this.email), this.name)

fun Member.toResponse(): MemberResponse =
    MemberResponse(id.value, email.value, name, status)
```

**Null safety:**
```kotlin
val member = repository.findById(id)
    ?: throw IllegalArgumentException("Member not found")
```

## Error Handling

### Maintain Spring Boot Error Handling

**Domain Layer:**
- Keep `IllegalArgumentException`

**Application Layer:**
- Keep `@Transactional.rollbackOn`

**Presentation Layer:**
- Keep `@ExceptionHandler`

**Optional Kotlin Enhancement:**
```kotlin
sealed class Result<out T>
data class Success<T>(val value: T) : Result<T>()
data class Failure(val error: Error) : Result<Nothing>()
```

## Testing

### Maintain JUnit 5 + AssertJ

```kotlin
class MemberTest {
    @Test
    fun `should create member with valid email`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")

        assertThat(member.email).isEqualTo(email)
        assertThat(member.status).isEqualTo(MemberStatus.ACTIVE)
    }
}
```

### Kotlin Test Features

**1. Backtick test names:**
```kotlin
@Test
fun `이메일이_null이면_예외발생`() { }
```

**2. Mockk (optional, replace Mockito):**
```kotlin
val repository = mockk<MemberRepository>()
every { repository.save(any()) } returns member
```

## Technology Stack

| Item | Version |
|------|---------|
| Kotlin | 2.1.0 |
| Spring Boot | 3.3.1 |
| Gradle | Kotlin DSL (.kts) |
| JUnit | 5.10.3 |
| AssertJ | 3.25.3 |

## Success Criteria

1. All Java files converted to Kotlin
2. All tests pass after conversion
3. Build succeeds with `./gradlew build`
4. Code uses Kotlin idioms (data classes, null safety, extension functions)
5. No functionality loss from original Java project
