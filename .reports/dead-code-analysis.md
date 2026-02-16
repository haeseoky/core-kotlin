# Dead Code Analysis Report

**Generated:** 2026-02-16
**Project:** core-kotlin
**Analysis Scope:** All Kotlin source files

## Summary

| Category | Count | Files |
|----------|-------|-------|
| **SAFE TO DELETE** | 3 | Unused test config, unused interfaces |
| **CAUTION** | 1 | Implemented adapter not in use |
| **DANGER** | 0 | None |
| **TOTAL** | 4 | - |

---

## SAFE to Delete

### 1. `core-infra/src/test/kotlin/com/ocean/member/core/infra/config/TestConfig.kt`

**Reason:** Not imported or referenced anywhere

**Evidence:**
```bash
# No import statements found
grep -r "import.*TestConfig" --include="*.kt"
# Result: Only finds MemberInfraTestConfig, not TestConfig
```

**Content:**
```kotlin
package com.ocean.member.core.infra.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@SpringBootApplication
class TestConfig
```

**Impact:** None - already using `MemberInfraTestConfig`

---

### 2. `core-domain/src/main/kotlin/com/ocean/member/core/domain/port/MemberEventPort.kt`

**Reason:** Interface defined but never implemented or used

**Evidence:**
```bash
# Only found in its own file
grep -r "MemberEventPort" --include="*.kt"
# Result: Only the file itself
```

**Content:**
```kotlin
interface MemberEventPort {
    fun publishMemberCreated(member: Member)
    fun publishMemberStatusChanged(member: Member)
}
```

**Impact:** None - Kafka events handled directly in services

---

### 3. `core-application/src/main/kotlin/com/ocean/member/core/application/port/MemberEventConsumer.kt`

**Reason:** Interface defined but never implemented or used

**Evidence:**
```bash
# Only found in its own file
grep -r "MemberEventConsumer" --include="*.kt"
# Result: Only the file itself
```

**Content:**
```kotlin
interface MemberEventConsumer {
    fun onMemberCreated(memberId: Long, email: String, name: String)
    fun onMemberStatusChanged(memberId: Long, oldStatus: String, newStatus: String)
}
```

**Impact:** None - no event consumers implemented yet

---

## CAUTION (Manual Review Recommended)

### 4. `core-infra/src/main/kotlin/com/ocean/member/core/infra/adapter/MemberRepositoryAdapter.kt`

**Reason:** Implements `MemberRepository` but not used as Spring Bean

**Evidence:**
```bash
# MemberRepository interface is used in services
grep -r "MemberRepository" --include="*.kt"
# Result: Used in MemberCommandService, MemberQueryService

# But MemberRepositoryAdapter is never referenced
grep -r "MemberRepositoryAdapter" --include="*.kt"
# Result: Only the file itself
```

**Current Architecture:**
```
Services (Command/Query) → MemberRepository (interface)
                                      ↑
                               No implementation found!
```

**Note:** Services may be using repositories directly or Spring Data JPA auto-configuration. This adapter appears to be legacy code from a previous architecture iteration.

**Recommendation:** Safe to delete if using `MemberJpaRepository` directly in services

---

## DANGER (Do Not Delete)

| File | Reason |
|------|--------|
| `core-domain/.../repository/MemberRepository.kt` | Used in Command/Query services |
| `core-infra/.../persistence/MemberJpaRepository.kt` | Spring Data JPA repository |

---

## Recommendations

1. ✅ **Delete immediately:** TestConfig.kt
2. ✅ **Delete immediately:** MemberEventPort.kt
3. ✅ **Delete immediately:** MemberEventConsumer.kt
4. ⚠️ **Review before delete:** MemberRepositoryAdapter.kt

5. **Future improvements:**
   - Implement proper hexagonal architecture with explicit adapters
   - Add Knip or similar dead code detection tool
   - Add dependency analysis to CI/CD
