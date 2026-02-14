package com.ocean.member.core.infra.repository

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import com.ocean.member.core.infra.entity.MemberEntity
import com.ocean.member.core.infra.persistence.MemberJpaRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private lateinit var memberJpaRepository: MemberJpaRepository

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    private fun createMember(email: String, name: String): Member {
        val member = Member.create(
            email = Email.of(email),
            name = name,
            status = MemberStatus.ACTIVE
        )

        testEntityManager.persist(member.toEntity())
        testEntityManager.flush()

        return member
    }

    @Nested
    @DisplayName("MemberRepository 저장소")
    inner class SaveMemberTests {

        @Test
        @DisplayName("회원 저장 성공")
        fun save_savesToDatabase_success() {
            // given
            val email = "save@example.com"
            val name = "저장테스트"
            val member = createMember(email, name)

            // when
            val result = memberJpaRepository.save(member.toEntity())

            // then
            val savedMember = memberJpaRepository.findById(result.id!!)
            assertThat(savedMember).isPresent
            assertThat(savedMember.get().email).isEqualTo(member.email.value)
            assertThat(savedMember.get().name).isEqualTo(member.name)
        }

        @Test
        @DisplayName("회원 저장 중복 이메일 체크")
        fun save_duplicateEmail_throwsException() {
            // given
            val email = "duplicate@example.com"
            createMember(email, "중복이메일")

            // when & then
            val exception = assertThrows<DataIntegrityViolationException> {
                val duplicateMember = Member.create(
                    email = Email.of(email),
                    name = "다른이름",
                    status = MemberStatus.ACTIVE
                )
                memberJpaRepository.save(duplicateMember.toEntity())
                memberJpaRepository.flush()
            }

            assertThat(exception).isNotNull()
        }
    }

    @Nested
    @DisplayName("회원 조회")
    inner class FindMemberTests {

        @Test
        @DisplayName("이메일로 회원 조회")
        fun findByEmail_returnsMember() {
            // given
            val email = "test@example.com"
            createMember(email, "테스트회원")

            // when
            val member = memberJpaRepository.findByEmail(email)

            // then
            assertThat(member).isNotNull()
            assertThat(member!!.email).isEqualTo(email)
        }

        @Test
        @DisplayName("없는 이메일로 조회 시 null 반환")
        fun findByEmail_notFound_returnsNull() {
            // given
            val email = "notfound@example.com"

            // when
            val member = memberJpaRepository.findByEmail(email)

            // then
            assertThat(member).isNull()
        }

        @Test
        @DisplayName("ID로 회원 조회")
        fun findById_returnsMember() {
            // given
            val email = "findbyid@example.com"
            val member = createMember(email, "ID조회테스트")
            val memberId = member.id!!

            // when
            val foundMember = memberJpaRepository.findById(memberId)

            // then
            assertThat(foundMember).isPresent
            assertThat(foundMember.get().id).isEqualTo(memberId)
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 값 반환")
        fun findById_notFound_returnsEmpty() {
            // given
            val memberId = 999999L

            // when
            val result = memberJpaRepository.findById(memberId)

            // then
            assertThat(result).isEmpty
        }

        @Test
        @DisplayName("모든 회원 조회")
        fun findAll_returnsMembers() {
            // given
            createMember("member1@example.com", "회원1")
            createMember("member2@example.com", "회원2")

            // when
            val members = memberJpaRepository.findAll()

            // then
            assertThat(members).hasSizeGreaterThanOrEqualTo(2)
        }
    }
    }

    @Nested
    @DisplayName("회원 존재 여부 확인")
    inner class ExistsMemberTests {

        @Test
        @DisplayName("존재하는 이메일 확인")
        fun existsByEmail_returnsTrue() {
            // given
            val email = "exists@example.com"
            createMember(email, "존재회원")

            // when
            val result = memberJpaRepository.existsByEmail(email)

            // then
            assertThat(result).isTrue()
        }

        @Test
        @DisplayName("존재하지 않는 이메일 확인")
        fun existsByEmail_returnsFalse() {
            // given
            val email = "notfound@example.com"

            // when
            val result = memberJpaRepository.existsByEmail(email)

            // then
            assertThat(result).isFalse()
        }

        @Test
        @DisplayName("회원 수 카운트")
        fun count_returnsCount() {
            // given
            createMember("count1@example.com", "카운트1")
            createMember("count2@example.com", "카운트2")

            // when
            val count = memberJpaRepository.count()

            // then
            assertThat(count).isGreaterThan(0)
        }
    }

    @Nested
    @DisplayName("회원 삭제")
    inner class DeleteMemberTests {

        @Test
        @DisplayName("ID로 회원 삭제 성공")
        fun deleteById_success() {
            // given
            val email = "delete@example.com"
            val name = "삭제대상"
            val member = createMember(email, name)
            val memberId = member.id!!

            // when
            memberJpaRepository.deleteById(memberId)

            // then
            val result = memberJpaRepository.findById(memberId)
            assertThat(result).isEmpty
        }
    }
    }
}
