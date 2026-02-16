package com.ocean.member.core.infra.repository

import com.ocean.member.core.infra.MemberInfraTestConfig
import com.ocean.member.core.infra.entity.MemberEntity
import com.ocean.member.core.infra.persistence.MemberJpaRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [MemberInfraTestConfig::class])
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MemberRepositoryTest {

    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    @DisplayName("회원 저장 성공")
    fun save_savesToDatabase_success() {
        // given
        val entity = MemberEntity(
            email = "test@example.com",
            name = "테스트",
            status = com.ocean.member.core.domain.model.valueobject.MemberStatus.ACTIVE
        )

        // when
        val result = memberJpaRepository.save(entity)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.email).isEqualTo("test@example.com")
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    fun findByEmail_returnsMember() {
        // given
        val entity = MemberEntity(
            email = "find@example.com",
            name = "조회테스트",
            status = com.ocean.member.core.domain.model.valueobject.MemberStatus.ACTIVE
        )
        entityManager.persist(entity)
        entityManager.flush()

        // when
        val found = memberJpaRepository.findByEmail("find@example.com")

        // then
        assertThat(found).isNotNull()
        assertThat(found!!.email).isEqualTo("find@example.com")
    }

    @Test
    @DisplayName("없는 이메일로 조회 시 null 반환")
    fun findByEmail_notFound_returnsNull() {
        // when
        val found = memberJpaRepository.findByEmail("notfound@example.com")

        // then
        assertThat(found).isNull()
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    fun existsByEmail_returnsTrue() {
        // given
        val entity = MemberEntity(
            email = "exists@example.com",
            name = "존재확인",
            status = com.ocean.member.core.domain.model.valueobject.MemberStatus.ACTIVE
        )
        entityManager.persist(entity)
        entityManager.flush()

        // when
        val exists = memberJpaRepository.existsByEmail("exists@example.com")

        // then
        assertThat(exists).isTrue()
    }
}
