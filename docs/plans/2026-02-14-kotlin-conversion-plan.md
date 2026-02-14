# Java to Kotlin Conversion Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Convert Java-based Spring Boot member management service to Kotlin while maintaining DDD architecture and all functionality.

**Architecture:** Multi-module Gradle project with Kotlin DSL. Convert module by module: domain → application → infra → presentation. Use IntelliJ auto-conversion with Kotlin refactoring.

**Tech Stack:** Kotlin 2.1.0, Spring Boot 3.3.1, Gradle Kotlin DSL, JUnit 5, AssertJ

---

## Task 1: Project Skeleton Setup

**Files:**
- Create: `build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `gradle/libs.versions.toml`
- Create: `gradle.properties`

**Step 1: Create root build.gradle.kts**

```kotlin
plugins {
    id("java")
    kotlin("jvm") version "2.1.0"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ocean.member"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":core-application"))
    implementation(project(":core-infra"))
    implementation(project(":core-presentation"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql:42.7.9")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

subprojects {
    tasks.withType<Jar>().configureEach {
        enabled = true
    }
}
```

**Step 2: Create settings.gradle.kts**

```kotlin
rootProject.name = "core-kotlin"

include("core-domain")
include("core-application")
include("core-infra")
include("core-presentation")
```

**Step 3: Create gradle/libs.versions.toml**

```toml
[versions]
kotlin = "2.1.0"
spring-boot = "3.3.1"
junit = "5.10.3"
assertj = "3.25.3"

[libraries]
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
```

**Step 4: Create gradle.properties**

```properties
kotlin.code.style=official
kotlin.incremental=true
```

**Step 5: Initialize gradle wrapper**

Run: `gradle wrapper --gradle-version 8.11`
Expected: gradle wrapper files created

**Step 6: Verify build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL (with failing module tests)

---

## Task 2: Core Domain Module Setup

**Files:**
- Create: `core-domain/build.gradle.kts`
- Create: `core-domain/src/main/kotlin/` directory
- Create: `core-domain/src/test/kotlin/` directory

**Step 1: Create core-domain/build.gradle.kts**

```kotlin
plugins {
    id("java-library")
    kotlin("jvm")
}

group = "com.ocean.member"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Verify module build**

Run: `./gradlew :core-domain:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add build.gradle.kts settings.gradle.kts gradle/ core-domain/build.gradle.kts
git commit -m "feat: setup project skeleton with kotlin DSL"
```

---

## Task 3: Convert MemberId Value Object

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/model/MemberId.kt`
- Test: `core-domain/src/test/kotlin/com/ocean/member/core/domain/model/MemberIdTest.kt`

**Step 1: Write the failing test**

```kotlin
package com.ocean.member.core.domain.model

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class MemberIdTest {

    @Test
    fun `should generate unique id`() {
        val id1 = MemberId.generate()
        val id2 = MemberId.generate()

        assertThat(id1).isNotEqualTo(id2)
    }

    @Test
    fun `should create id from value`() {
        val value = 123L
        val id = MemberId.of(value)

        assertThat(id.value()).isEqualTo(value)
    }

    @Test
    fun `should be equal when same value`() {
        val value = 123L
        val id1 = MemberId.of(value)
        val id2 = MemberId.of(value)

        assertThat(id1).isEqualTo(id2)
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode())
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :core-domain:test --tests MemberIdTest`
Expected: FAIL with "Unresolved reference: MemberId"

**Step 3: Write minimal implementation**

```kotlin
package com.ocean.member.core.domain.model

import java.util.concurrent.atomic.AtomicLong

@JvmInline
value class MemberId(private val value: Long) {
    companion object {
        private val counter = AtomicLong(System.currentTimeMillis())

        fun generate(): MemberId = MemberId(counter.incrementAndGet())
    }

    fun value(): Long = value
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :core-domain:test --tests MemberIdTest`
Expected: PASS

**Step 5: Commit**

```bash
git add core-domain/src/main/kotlin/.../MemberId.kt core-domain/src/test/kotlin/.../MemberIdTest.kt
git commit -m "feat: add MemberId value object"
```

---

## Task 4: Convert Email Value Object

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/model/valueobject/Email.kt`
- Test: `core-domain/src/test/kotlin/com/ocean/member/core/domain/model/valueobject/EmailTest.kt`

**Step 1: Write the failing test**

```kotlin
package com.ocean.member.core.domain.model.valueobject

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class EmailTest {

    @Test
    fun `should create valid email`() {
        val email = Email.of("test@example.com")

        assertThat(email.value()).isEqualTo("test@example.com")
    }

    @Test
    fun `should trim and lowercase email`() {
        val email = Email.of("  TEST@EXAMPLE.COM  ")

        assertThat(email.value()).isEqualTo("test@example.com")
    }

    @Test
    fun `should throw when email is null`() {
        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { Email.of(null) }
            .withMessageContaining("cannot be null or blank")
    }

    @Test
    fun `should throw when email is blank`() {
        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { Email.of("   ") }
            .withMessageContaining("cannot be null or blank")
    }

    @Test
    fun `should throw when email is invalid`() {
        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { Email.of("invalid-email") }
            .withMessageContaining("Invalid email format")
    }

    @Test
    fun `should be equal when same value`() {
        val email1 = Email.of("test@example.com")
        val email2 = Email.of("test@example.com")

        assertThat(email1).isEqualTo(email2)
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode())
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :core-domain:test --tests EmailTest`
Expected: FAIL with "Unresolved reference: Email"

**Step 3: Write minimal implementation**

```kotlin
package com.ocean.member.core.domain.model.valueobject

@JvmInline
value class Email(val value: String) {

    companion object {
        private val EMAIL_REGEX = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

        fun of(email: String?): Email {
            requireNotNull(email) { "Email value cannot be null" }
            require(email.isNotBlank()) { "Email cannot be null or blank" }

            val trimmed = email.trim().lowercase()
            require(isValidEmail(trimmed)) { "Invalid email format: $email" }

            return Email(trimmed)
        }

        private fun isValidEmail(email: String): Boolean {
            if (".." in email) return false
            return EMAIL_REGEX.matches(email)
        }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :core-domain:test --tests EmailTest`
Expected: PASS

**Step 5: Commit**

```bash
git add core-domain/src/main/kotlin/.../Email.kt core-domain/src/test/kotlin/.../EmailTest.kt
git commit -m "feat: add Email value object"
```

---

## Task 5: Convert MemberStatus Enum

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/model/valueobject/MemberStatus.kt`
- Test: `core-domain/src/test/kotlin/com/ocean/member/core/domain/model/valueobject/MemberStatusTest.kt`

**Step 1: Write the failing test**

```kotlin
package com.ocean.member.core.domain.model.valueobject

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class MemberStatusTest {

    @Test
    fun `ACTIVE can transit to INACTIVE`() {
        assertThat(MemberStatus.ACTIVE.canTransitTo(MemberStatus.INACTIVE)).isTrue()
    }

    @Test
    fun `ACTIVE can transit to SUSPENDED`() {
        assertThat(MemberStatus.ACTIVE.canTransitTo(MemberStatus.SUSPENDED)).isTrue()
    }

    @Test
    fun `INACTIVE cannot transit to ACTIVE`() {
        assertThat(MemberStatus.INACTIVE.canTransitTo(MemberStatus.ACTIVE)).isFalse()
    }

    @Test
    fun `INACTIVE can transit to SUSPENDED`() {
        assertThat(MemberStatus.INACTIVE.canTransitTo(MemberStatus.SUSPENDED)).isTrue()
    }

    @Test
    fun `SUSPENDED can transit to ACTIVE`() {
        assertThat(MemberStatus.SUSPENDED.canTransitTo(MemberStatus.ACTIVE)).isTrue()
    }

    @Test
    fun `SUSPENDED can transit to INACTIVE`() {
        assertThat(MemberStatus.SUSPENDED.canTransitTo(MemberStatus.INACTIVE)).isTrue()
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :core-domain:test --tests MemberStatusTest`
Expected: FAIL with "Unresolved reference: MemberStatus"

**Step 3: Write minimal implementation**

```kotlin
package com.ocean.member.core.domain.model.valueobject

enum class MemberStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED;

    fun canTransitTo(newStatus: MemberStatus): Boolean {
        return when (this) {
            ACTIVE -> newStatus == INACTIVE || newStatus == SUSPENDED
            INACTIVE -> newStatus == SUSPENDED
            SUSPENDED -> newStatus == ACTIVE || newStatus == INACTIVE
        }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :core-domain:test --tests MemberStatusTest`
Expected: PASS

**Step 5: Commit**

```bash
git add core-domain/src/main/kotlin/.../MemberStatus.kt core-domain/src/test/kotlin/.../MemberStatusTest.kt
git commit -m "feat: add MemberStatus enum"
```

---

## Task 6: Convert Member Entity

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/model/Member.kt`
- Test: `core-domain/src/test/kotlin/com/ocean/member/core/domain/model/MemberTest.kt`

**Step 1: Write the failing test**

```kotlin
package com.ocean.member.core.domain.model

import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

class MemberTest {

    @Test
    fun `should create member with ACTIVE status`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")

        assertThat(member.status).isEqualTo(MemberStatus.ACTIVE)
        assertThat(member.email).isEqualTo(email)
        assertThat(member.name).isEqualTo("Test User")
        assertThat(member.isActive()).isTrue()
    }

    @Test
    fun `should throw when name is blank`() {
        val email = Email.of("test@example.com")

        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { Member.create(email, "   ") }
            .withMessageContaining("Name cannot be null or blank")
    }

    @Test
    fun `should throw when name exceeds 100 characters`() {
        val email = Email.of("test@example.com")
        val longName = "a".repeat(101)

        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { Member.create(email, longName) }
            .withMessageContaining("Name cannot exceed 100 characters")
    }

    @Test
    fun `should update information`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Old Name")

        member.updateInformation("New Name")

        assertThat(member.name).isEqualTo("New Name")
    }

    @Test
    fun `should change status`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")

        member.changeStatus(MemberStatus.INACTIVE)

        assertThat(member.status).isEqualTo(MemberStatus.INACTIVE)
    }

    @Test
    fun `should throw when status transition is invalid`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")
        member.changeStatus(MemberStatus.INACTIVE)

        assertThatThrownBy<IllegalStateException>()
            .isThrownBy { member.changeStatus(MemberStatus.ACTIVE) }
            .withMessageContaining("Cannot transition")
    }

    @Test
    fun `should soft delete`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")

        member.softDelete()

        assertThat(member.isDeleted()).isTrue()
        assertThat(member.status).isEqualTo(MemberStatus.INACTIVE)
    }

    @Test
    fun `should throw when soft delete already deleted`() {
        val email = Email.of("test@example.com")
        val member = Member.create(email, "Test User")
        member.softDelete()

        assertThatThrownBy<IllegalStateException>()
            .isThrownBy { member.softDelete() }
            .withMessageContaining("already deleted")
    }

    @Test
    fun `should restore member`() {
        val member = Member.restore(
            1L,
            Email.of("test@example.com"),
            "Test User",
            MemberStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        )

        assertThat(member.id.value()).isEqualTo(1L)
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :core-domain:test --tests MemberTest`
Expected: FAIL with "Unresolved reference: Member"

**Step 3: Write minimal implementation**

```kotlin
package com.ocean.member.core.domain.model

import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import java.time.LocalDateTime
import java.util.Objects

class Member(
    val id: MemberId,
    val email: Email,
    var name: String,
    var status: MemberStatus,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?
) {

    companion object {
        fun create(email: Email, name: String): Member {
            val now = LocalDateTime.now()
            return Member(
                id = MemberId.generate(),
                email = email,
                name = name,
                status = MemberStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
                deletedAt = null
            )
        }

        fun restore(
            id: Long,
            email: Email,
            name: String,
            status: MemberStatus,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            deletedAt: LocalDateTime?
        ): Member {
            return Member(
                id = MemberId.of(id),
                email = email,
                name = validateName(name),
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }

        private fun validateName(name: String?): String {
            require(name != null && name.isNotBlank()) { "Name cannot be null or blank" }
            require(name.length <= 100) { "Name cannot exceed 100 characters" }
            return name.trim()
        }
    }

    init {
        // Validation in primary constructor
        require(name.isNotBlank()) { "Name cannot be null or blank" }
        require(name.length <= 100) { "Name cannot exceed 100 characters" }
    }

    fun updateInformation(newName: String) {
        this.name = validateName(newName)
        this.updatedAt = LocalDateTime.now()
    }

    fun changeStatus(newStatus: MemberStatus) {
        requireNotNull(newStatus) { "New status cannot be null" }
        require(this.status.canTransitTo(newStatus)) {
            "Cannot transition from ${this.status} to $newStatus"
        }
        this.status = newStatus
        this.updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        require(deletedAt == null) { "Member is already deleted" }
        this.status = MemberStatus.INACTIVE
        this.deletedAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun isActive(): Boolean = status == MemberStatus.ACTIVE && deletedAt == null

    fun isDeleted(): Boolean = deletedAt != null

    fun getIdValue(): Long = id.value()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String {
        return "Member(" +
                "id=$id, " +
                "email=$email, " +
                "name='$name', " +
                "status=$status, " +
                "createdAt=$createdAt, " +
                "updatedAt=$updatedAt, " +
                "deletedAt=$deletedAt)"
    }

    private companion object {
        fun validateName(name: String?): String {
            require(name != null && name.isNotBlank()) { "Name cannot be null or blank" }
            require(name.length <= 100) { "Name cannot exceed 100 characters" }
            return name.trim()
        }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :core-domain:test --tests MemberTest`
Expected: PASS

**Step 5: Commit**

```bash
git add core-domain/src/main/kotlin/.../Member.kt core-domain/src/test/kotlin/.../MemberTest.kt
git commit -m "feat: add Member entity"
```

---

## Task 7: Convert MemberRepository Interface

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/model/repository/MemberRepository.kt`

**Step 1: Create repository interface**

```kotlin
package com.ocean.member.core.domain.model.repository

import com.ocean.member.core.domain.model.Member
import java.util.Optional

interface MemberRepository {

    fun save(member: Member): Member

    fun findById(id: Long): Optional<Member>

    fun findAll(): List<Member>

    fun existsByEmail(email: String): Boolean

    fun deleteById(id: Long)
}
```

**Step 2: Verify build**

Run: `./gradlew :core-domain:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-domain/src/main/kotlin/.../MemberRepository.kt
git commit -m "feat: add MemberRepository interface"
```

---

## Task 8: Convert MemberEventPort Interface

**Files:**
- Create: `core-domain/src/main/kotlin/com/ocean/member/core/domain/port/MemberEventPort.kt`

**Step 1: Create event port interface**

```kotlin
package com.ocean.member.core.domain.port

import com.ocean.member.core.domain.model.Member

interface MemberEventPort {

    fun publishMemberCreated(member: Member)

    fun publishMemberStatusChanged(member: Member)
}
```

**Step 2: Verify build**

Run: `./gradlew :core-domain:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-domain/src/main/kotlin/.../MemberEventPort.kt
git commit -m "feat: add MemberEventPort interface"
```

---

## Task 9: Core Application Module Setup

**Files:**
- Create: `core-application/build.gradle.kts`
- Create: `core-application/src/main/kotlin/` directory
- Create: `core-application/src/test/kotlin/` directory

**Step 1: Create core-application/build.gradle.kts**

```kotlin
plugins {
    id("java-library")
    kotlin("jvm")
}

group = "com.ocean.member"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core-domain"))

    api("org.springframework.boot:spring-boot-starter-data-jpa:3.3.1")

    runtimeOnly("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql:42.7.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Verify module build**

Run: `./gradlew :core-application:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-application/build.gradle.kts
git commit -m "feat: setup core-application module"
```

---

## Task 10: Convert MemberCommandService

**Files:**
- Create: `core-application/src/main/kotlin/com/ocean/member/core/application/service/MemberCommandService.kt`
- Test: `core-application/src/test/kotlin/com/ocean/member/core/application/service/MemberCommandServiceTest.kt`

**Step 1: Write the failing test**

```kotlin
package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import com.ocean.member.core.domain.model.repository.MemberRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

@ExtendWith(MockitoExtension::class)
class MemberCommandServiceTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @InjectMocks
    private lateinit var memberCommandService: MemberCommandService

    @Test
    fun `should create member successfully`() {
        val email = "test@example.com"
        val name = "Test User"

        whenever(memberRepository.existsByEmail(email)).thenReturn(false)
        whenever(memberRepository.save(any())).thenAnswer { invocation -> invocation.arguments[0] as Member }

        val result = memberCommandService.createMember(email, name)

        assertThat(result.email.value()).isEqualTo(email)
        assertThat(result.name).isEqualTo(name)
        assertThat(result.status).isEqualTo(MemberStatus.ACTIVE)
        verify(memberRepository).save(any())
    }

    @Test
    fun `should throw when email already exists`() {
        val email = "test@example.com"

        whenever(memberRepository.existsByEmail(email)).thenReturn(true)

        assertThatThrownBy<IllegalArgumentException>()
            .isThrownBy { memberCommandService.createMember(email, "Name") }
            .withMessageContaining("already exists")

        verify(memberRepository, never()).save(any())
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :core-application:test --tests MemberCommandServiceTest`
Expected: FAIL with "Unresolved reference: MemberCommandService"

**Step 3: Write minimal implementation**

```kotlin
package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import com.ocean.member.core.domain.model.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandService(
    private val memberRepository: MemberRepository
) {

    fun createMember(email: String, name: String): Member {
        val validatedEmail = Email.of(email)

        require(!memberRepository.existsByEmail(validatedEmail.value())) {
            "Member with email '$email' already exists"
        }

        val member = Member.create(validatedEmail, name)
        return memberRepository.save(member)
    }

    fun updateMemberInformation(memberId: Long, newName: String): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        require(newName.isNotBlank()) { "Name cannot be null or blank" }

        member.updateInformation(newName)
        return memberRepository.save(member)
    }

    fun changeMemberStatus(memberId: Long, newStatus: MemberStatus): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        requireNotNull(newStatus) { "New status cannot be null" }

        member.changeStatus(newStatus)
        return memberRepository.save(member)
    }

    fun deleteMember(memberId: Long) {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)
        member.softDelete()
        memberRepository.save(member)
    }

    fun permanentlyDeleteMember(memberId: Long) {
        if (!memberRepository.findById(memberId).isPresent) {
            throw IllegalArgumentException("Member not found with ID: $memberId")
        }
        memberRepository.deleteById(memberId)
    }

    private fun validateId(id: Long?) {
        require(id != null && id > 0) { "Invalid member ID: $id" }
    }

    private fun findMemberOrThrow(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :core-application:test --tests MemberCommandServiceTest`
Expected: PASS

**Step 5: Commit**

```bash
git add core-application/src/main/kotlin/.../MemberCommandService.kt core-application/src/test/kotlin/.../MemberCommandServiceTest.kt
git commit -m "feat: add MemberCommandService"
```

---

## Task 11: Convert MemberQueryService

**Files:**
- Create: `core-application/src/main/kotlin/com/ocean/member/core/application/service/MemberQueryService.kt`

**Step 1: Create query service**

```kotlin
package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MemberQueryService(
    private val memberRepository: MemberRepository
) {

    fun getMemberById(memberId: Long): Member {
        validateId(memberId)
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }

    fun getAllMembers(): List<Member> {
        return memberRepository.findAll()
    }

    fun getActiveMembers(): List<Member> {
        return memberRepository.findAll().filter { it.isActive() }
    }

    private fun validateId(id: Long?) {
        require(id != null && id > 0) { "Invalid member ID: $id" }
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :core-application:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-application/src/main/kotlin/.../MemberQueryService.kt
git commit -m "feat: add MemberQueryService"
```

---

## Task 12: Convert MemberEventConsumer Interface

**Files:**
- Create: `core-application/src/main/kotlin/com/ocean/member/core/application/port/MemberEventConsumer.kt`

**Step 1: Create event consumer interface**

```kotlin
package com.ocean.member.core.application.port

interface MemberEventConsumer {

    fun onMemberCreated(memberId: Long, email: String, name: String)

    fun onMemberStatusChanged(memberId: Long, oldStatus: String, newStatus: String)
}
```

**Step 2: Verify build**

Run: `./gradlew :core-application:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-application/src/main/kotlin/.../MemberEventConsumer.kt
git commit -m "feat: add MemberEventConsumer interface"
```

---

## Task 13: Core Infra Module Setup

**Files:**
- Create: `core-infra/build.gradle.kts`
- Create: `core-infra/src/main/kotlin/` directory
- Create: `core-infra/src/test/kotlin/` directory

**Step 1: Create core-infra/build.gradle.kts**

```kotlin
plugins {
    id("java-library")
    kotlin("jvm")
}

group = "com.ocean.member"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core-domain"))

    api("org.springframework.boot:spring-boot-starter-data-jpa:3.3.1")

    runtimeOnly("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql:42.7.9")

    compileOnly("org.springframework:spring-context:6.1.6")
    compileOnly("org.springframework:spring-tx:6.1.6")
    compileOnly("jakarta.transaction:jakarta.transaction-api:2.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Verify module build**

Run: `./gradlew :core-infra:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-infra/build.gradle.kts
git commit -m "feat: setup core-infra module"
```

---

## Task 14: Convert MemberEntity

**Files:**
- Create: `core-infra/src/main/kotlin/com/ocean/member/core/infra/entity/MemberEntity.kt`

**Step 1: Create JPA entity**

```kotlin
package com.ocean.member.core.infra.entity

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.MemberId
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class MemberEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MemberStatus,

    @Column(nullable = false, name = "created_at")
    var createdAt: LocalDateTime,

    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) {

    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun toDomain(): Member {
        return Member.restore(
            id = id ?: throw IllegalStateException("ID cannot be null"),
            email = Email.of(email),
            name = name,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :core-infra:compileKotlin`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-infra/src/main/kotlin/.../MemberEntity.kt
git commit -m "feat: add MemberEntity"
```

---

## Task 15: Convert MemberRepository Implementation

**Files:**
- Create: `core-infra/src/main/kotlin/com/ocean/member/core/infra/persistence/MemberRepositoryImpl.kt`

**Step 1: Create repository implementation**

```kotlin
package com.ocean.member.core.infra.persistence

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.infra.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepositoryImpl : JpaRepository<MemberEntity, Long> {

    fun existsByEmail(email: String): Boolean

    fun toDomainRepository(): MemberRepository {
        return object : MemberRepository {
            override fun save(member: Member): Member {
                val entity = MemberEntity().apply {
                    id = if (member.getIdValue() == 0L) null else member.getIdValue()
                    email = member.email.value()
                    name = member.name
                    status = member.status
                    createdAt = member.createdAt
                    updatedAt = member.updatedAt
                    deletedAt = member.deletedAt
                }
                val saved = this@MemberRepositoryImpl.save(entity)
                return saved.toDomain()
            }

            override fun findById(id: Long): Optional<Member> {
                return this@MemberRepositoryImpl.findById(id).map { it.toDomain() }
            }

            override fun findAll(): List<Member> {
                return this@MemberRepositoryImpl.findAll().map { it.toDomain() }
            }

            override fun existsByEmail(email: String): Boolean {
                return this@MemberRepositoryImpl.existsByEmail(email)
            }

            override fun deleteById(id: Long) {
                this@MemberRepositoryImpl.deleteById(id)
            }
        }
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :core-infra:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-infra/src/main/kotlin/.../MemberRepositoryImpl.kt
git commit -m "feat: add MemberRepository implementation"
```

---

## Task 16: Core Presentation Module Setup

**Files:**
- Create: `core-presentation/build.gradle.kts`
- Create: `core-presentation/src/main/kotlin/` directory
- Create: `core-presentation/src/test/kotlin/` directory

**Step 1: Create core-presentation/build.gradle.kts**

```kotlin
plugins {
    id("java-library")
    kotlin("jvm")
}

group = "com.ocean.member"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core-application"))

    api("org.springframework.boot:spring-boot-starter-web:3.3.1")
    api("org.springframework.boot:spring-boot-starter-validation:3.3.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Verify module build**

Run: `./gradlew :core-presentation:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-presentation/build.gradle.kts
git commit -m "feat: setup core-presentation module"
```

---

## Task 17: Convert DTOs

**Files:**
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/dto/MemberRequest.kt`
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/dto/MemberResponse.kt`
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/dto/MemberStatusUpdateRequest.kt`
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/dto/ErrorResponse.kt`

**Step 1: Create DTOs**

```kotlin
// MemberRequest.kt
package com.ocean.member.core.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class MemberRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String
)

// MemberResponse.kt
package com.ocean.member.core.presentation.dto

import com.ocean.member.core.domain.model.valueobject.MemberStatus

data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
    val status: MemberStatus,
    val createdAt: String,
    val updatedAt: String
)

// MemberStatusUpdateRequest.kt
package com.ocean.member.core.presentation.dto

import jakarta.validation.constraints.NotNull

data class MemberStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: MemberStatus
)

// ErrorResponse.kt
package com.ocean.member.core.presentation.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null
)
```

**Step 2: Verify build**

Run: `./gradlew :core-presentation:compileKotlin`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-presentation/src/main/kotlin/.../dto/
git commit -m "feat: add DTOs"
```

---

## Task 18: Convert MemberController

**Files:**
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/controller/MemberController.kt`

**Step 1: Create controller**

```kotlin
package com.ocean.member.core.presentation.controller

import com.ocean.member.core.application.service.MemberCommandService
import com.ocean.member.core.application.service.MemberQueryService
import com.ocean.member.core.presentation.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        val member = queryService.getMemberById(id)
        return ResponseEntity.ok(member.toResponse())
    }

    @GetMapping
    fun getAllMembers(): ResponseEntity<List<MemberResponse>> {
        val members = queryService.getAllMembers()
        return ResponseEntity.ok(members.map { it.toResponse() })
    }

    @PatchMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @Valid @RequestBody request: MemberRequest
    ): ResponseEntity<MemberResponse> {
        val member = commandService.updateMemberInformation(id, request.name)
        return ResponseEntity.ok(member.toResponse())
    }

    @PatchMapping("/{id}/status")
    fun changeStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: MemberStatusUpdateRequest
    ): ResponseEntity<MemberResponse> {
        val member = commandService.changeMemberStatus(id, request.status)
        return ResponseEntity.ok(member.toResponse())
    }

    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Void> {
        commandService.deleteMember(id)
        return ResponseEntity.noContent().build()
    }
}

// Extension function for mapping
fun com.ocean.member.core.domain.model.Member.toResponse(): MemberResponse {
    return MemberResponse(
        id = getIdValue(),
        email = email.value(),
        name = name,
        status = status,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
```

**Step 2: Verify build**

Run: `./gradlew :core-presentation:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-presentation/src/main/kotlin/.../MemberController.kt
git commit -m "feat: add MemberController"
```

---

## Task 19: Add Global Exception Handler

**Files:**
- Create: `core-presentation/src/main/kotlin/com/ocean/member/core/presentation/exception/GlobalExceptionHandler.kt`

**Step 1: Create exception handler**

```kotlin
package com.ocean.member.core.presentation.exception

import com.ocean.member.core.presentation.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid request",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        ex: IllegalStateException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflict",
            message = ex.message ?: "Invalid state",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :core-presentation:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add core-presentation/src/main/kotlin/.../GlobalExceptionHandler.kt
git commit -m "feat: add global exception handler"
```

---

## Task 20: Final Build and Test

**Step 1: Run full build**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL

**Step 2: Run all tests**

Run: `./gradlew test`
Expected: All tests PASS

**Step 3: Verify project structure**

Run: `./gradlew projects`
Expected: All 4 modules listed

**Step 4: Final commit**

```bash
git add .
git commit -m "feat: complete Java to Kotlin conversion"
```

---

## Success Criteria Checklist

- [ ] All Java files converted to Kotlin
- [ ] All tests pass after conversion
- [ ] Build succeeds with `./gradlew build`
- [ ] Code uses Kotlin idioms (data classes, null safety, extension functions)
- [ ] No functionality loss from original Java project

## References

- Original Java project: `/Users/haeseoky/workspace/2026/core`
- Design document: `docs/plans/2026-02-14-kotlin-conversion-design.md`
- Kotlin documentation: https://kotlinlang.org/docs/
- Spring Boot Kotlin: https://spring.io/guides/tutorials/spring-boot-kotlin/
